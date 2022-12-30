package com.inkapplications.ack.codec.weather

import com.inkapplications.ack.codec.TestData
import com.inkapplications.ack.codec.assertEquals
import com.inkapplications.ack.codec.timestamp.withUtcValues
import com.inkapplications.ack.structures.*
import inkapplications.spondee.measure.metric.pascals
import inkapplications.spondee.measure.metric.wattsPerSquareMeter
import inkapplications.spondee.measure.us.*
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.Hundreths
import inkapplications.spondee.structure.scale
import inkapplications.spondee.structure.toDouble
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class WeatherParserTest {
    private val parser = WeatherTransformer(TestData.timestampModule)
    @Test
    fun plainComplete() {
        val given = "/092345z4903.50N/07201.75W_220/004g005t-07r001p002P003h50b09900wRSW"

        val expectedTime = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = parser.parse(given)

        assertEquals(expectedTime, result.timestamp)
        assertEquals(49.0583, result.coordinates?.latitude?.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates?.longitude?.asDecimal, 0.0001)
        assertEquals(220.degrees, result.windData.direction)
        assertEquals(4.knots, result.windData.speed)
        assertEquals(5.milesPerHour, result.windData.gust)
        assertEquals((-7).fahrenheit, result.temperature)
        assertEquals(3.scale(Hundreths).inches, result.precipitation.rainToday)
        assertEquals(2.scale(Hundreths).inches, result.precipitation.rainLast24Hours)
        assertEquals(1.scale(Hundreths).inches, result.precipitation.rainLastHour)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.scale(Deka).pascals, result.pressure)
        assertEquals(symbolOf('/', '_'), result.symbol)
        assertNull(result.irradiance)
    }

    @Test
    fun plainMinimum() {
        val given = "!4903.50N/07201.75W_220/004g...t...r...p...P...h..b.....wRSW"

        val result = parser.parse(given)

        assertNull(result.timestamp)
        assertEquals(49.0583, result.coordinates?.latitude?.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates?.longitude?.asDecimal, 0.0001)
        assertEquals(220.degrees, result.windData.direction)
        assertEquals(4.knots, result.windData.speed)
        assertNull(result.windData.gust)
        assertNull(result.temperature)
        assertNull(result.precipitation.rainToday)
        assertNull(result.precipitation.rainLast24Hours)
        assertNull(result.precipitation.rainLastHour)
        assertNull(result.humidity)
        assertNull(result.pressure)
        assertEquals(symbolOf('/', '_'), result.symbol)
        assertNull(result.irradiance)
    }

    @Test
    fun plainLocation() {
        val given = "!4903.50N/07201.75W#225/000Hello World!"

        assertFails { parser.parse(given) }
    }

    @Test
    fun compressedComplete() {
        val given = "/092345z/5L!!<*e7_7P[g005t077r001p002P003h50b09900wRSW"

        val expectedTime = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = parser.parse(given)

        assertEquals(expectedTime, result.timestamp)
        assertEquals(49.5, result.coordinates?.latitude?.asDecimal, 0.1)
        assertEquals(-72.75, result.coordinates?.longitude?.asDecimal, 0.1)
        assertEquals(88.degrees, result.windData.direction)
        assertEquals(36.2, result.windData.speed!!.toKnots().toDouble(), 1e-1)
        assertEquals(5.milesPerHour, result.windData.gust)
        assertEquals(77.fahrenheit, result.temperature)
        assertEquals(3.scale(Hundreths).inches, result.precipitation.rainToday)
        assertEquals(2.scale(Hundreths).inches, result.precipitation.rainLast24Hours)
        assertEquals(1.scale(Hundreths).inches, result.precipitation.rainLastHour)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.scale(Deka).pascals, result.pressure)
        assertEquals(symbolOf('/', '_'), result.symbol)
        assertNull(result.irradiance)
    }

    @Test
    fun compressedEmpty() {
        val given = "//5L!!<*e7_ sTg...t...r...p...P...h..b     wRSW"

        val result = parser.parse(given)

        assertNull(result.timestamp)
        assertEquals(49.5, result.coordinates?.latitude?.asDecimal, 0.1)
        assertEquals(-72.75, result.coordinates?.longitude?.asDecimal, 0.1)
        assertNull(result.windData.direction)
        assertNull(result.windData.speed)
        assertNull(result.windData.gust)
        assertNull(result.temperature)
        assertNull(result.precipitation.rainToday)
        assertNull(result.precipitation.rainLast24Hours)
        assertNull(result.precipitation.rainLastHour)
        assertNull(result.humidity)
        assertNull(result.pressure)
        assertEquals(symbolOf('/', '_'), result.symbol)
        assertNull(result.irradiance)
    }

    @Test
    fun compressedLocation() {
        val given = "//5L!!<*e7_ sTHello World!"

        assertFails { parser.parse(given) }
    }

    @Test
    fun generate() {
        val given = PacketData.Weather(
            timestamp = Clock.System.now().withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            ),
            windData = WindData(
                direction = 220.degrees,
                speed = 4.milesPerHour,
                gust = 5.milesPerHour,
            ),
            precipitation = Precipitation(
                rainLastHour = 1.scale(Hundreths).inches,
                rainLast24Hours = 2.scale(Hundreths).inches,
                rainToday = 3.scale(Hundreths).inches,
                snowLast24Hours = 4.inches,
                rawRain = 789,
            ),
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('/', '_'),
            temperature = (-7).fahrenheit,
            humidity = 50.percent,
            pressure = 9900.scale(Deka).pascals,
            irradiance = 69.wattsPerSquareMeter,
        )

        val result = parser.generate(given, EncodingConfig(compression = EncodingPreference.Disfavored))

        assertEquals("/092345z4903.50N/07201.75W_220/004g005t-07r001p002P003h50b09900L069s004#789", result)
    }

    @Test
    fun generateEmpty() {
        val given = PacketData.Weather(
            timestamp = null,
            windData = WindData(
                direction = null,
                speed = null,
                gust = null,
            ),
            precipitation = Precipitation(),
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('/', '_'),
            temperature = null,
            humidity = null,
            pressure = null,
            irradiance = null,
        )

        val result = parser.generate(given, EncodingConfig(compression = EncodingPreference.Disfavored))

        assertEquals("!4903.50N/07201.75W_   /   ", result)
    }
}
