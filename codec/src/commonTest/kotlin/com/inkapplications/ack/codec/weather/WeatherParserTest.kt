package com.inkapplications.ack.codec.weather

import com.inkapplications.ack.codec.TestData
import com.inkapplications.ack.codec.assertEquals
import com.inkapplications.ack.codec.timestamp.withUtcValues
import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.unit.Knots
import inkapplications.spondee.measure.*
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.of
import inkapplications.spondee.structure.value
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
        assertEquals(Degrees.of(220), result.windData.direction)
        assertEquals(Knots.of(4), result.windData.speed)
        assertEquals(MilesPerHour.of(5), result.windData.gust)
        assertEquals(Fahrenheit.of((-7)), result.temperature)
        assertEquals(HundredthInches.of(3), result.precipitation.rainToday)
        assertEquals(HundredthInches.of(2), result.precipitation.rainLast24Hours)
        assertEquals(HundredthInches.of(1), result.precipitation.rainLastHour)
        assertEquals(WholePercentage.of(50), result.humidity)
        assertEquals(Pascals.of(Deka, 9900), result.pressure)
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
        assertEquals(Degrees.of(220), result.windData.direction)
        assertEquals(Knots.of(4), result.windData.speed)
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
        assertEquals(Degrees.of(88), result.windData.direction)
        assertEquals(36.2, result.windData.speed!!.value(Knots), 1e-1)
        assertEquals(MilesPerHour.of(5), result.windData.gust)
        assertEquals(Fahrenheit.of((77)), result.temperature)
        assertEquals(HundredthInches.of(3), result.precipitation.rainToday)
        assertEquals(HundredthInches.of(2), result.precipitation.rainLast24Hours)
        assertEquals(HundredthInches.of(1), result.precipitation.rainLastHour)
        assertEquals(WholePercentage.of(50), result.humidity)
        assertEquals(Pascals.of(Deka, 9900), result.pressure)
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
                direction = Degrees.of(220),
                speed = MilesPerHour.of(4),
                gust = MilesPerHour.of(5),
            ),
            precipitation = Precipitation(
                rainLastHour = HundredthInches.of(1),
                rainLast24Hours = HundredthInches.of(2),
                rainToday = HundredthInches.of(3),
                snowLast24Hours = Inches.of(4),
                rawRain = 789,
            ),
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('/', '_'),
            temperature = Fahrenheit.of(-7),
            humidity = WholePercentage.of(50),
            pressure = Pascals.of(Deka, 9900),
            irradiance = WattsPerSquareMeter.of(69),
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
