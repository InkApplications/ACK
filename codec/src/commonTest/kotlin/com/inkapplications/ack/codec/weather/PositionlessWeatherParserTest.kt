package com.inkapplications.ack.codec.weather

import com.inkapplications.ack.codec.TestData
import com.inkapplications.ack.codec.timestamp.withUtcValues
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.Precipitation
import com.inkapplications.ack.structures.WindData
import inkapplications.spondee.measure.metric.pascals
import inkapplications.spondee.measure.metric.wattsPerSquareMeter
import inkapplications.spondee.measure.us.fahrenheit
import inkapplications.spondee.measure.us.inches
import inkapplications.spondee.measure.us.milesPerHour
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.Hundreths
import inkapplications.spondee.structure.scale
import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class PositionlessWeatherParserTest {
    private val parser = PositionlessWeatherTransformer(TestData.timestampModule)
    
    @Test
    fun parse() {
        val given = "_10090556c220s004g005t077r001p002P003h50b09900wRSW"

        val result = parser.parse(given)
        val expectedTime = Clock.System.now()
            .withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 5,
                minute = 56,
                second = 0,
                nanosecond = 0
            )

        assertEquals(expectedTime, result.timestamp)
        assertEquals(220.degrees, result.windData.direction)
        assertEquals(4.milesPerHour, result.windData.speed)
        assertEquals(5.milesPerHour, result.windData.gust)
        assertEquals(1.scale(Hundreths).inches, result.precipitation.rainLastHour)
        assertEquals(2.scale(Hundreths).inches, result.precipitation.rainLast24Hours)
        assertEquals(3.scale(Hundreths).inches, result.precipitation.rainToday)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.scale(Deka).pascals, result.pressure)
        assertNull(result.irradiance)
        assertNull(result.symbol)
        assertNull(result.coordinates)
    }

    @Test
    fun empty() {
        val given = "_10090556c...s   g...t...P012Jim"

        val result = parser.parse(given)
        val expectedTime = Clock.System.now()
            .withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 5,
                minute = 56,
                second = 0,
                nanosecond = 0
            )

        assertEquals(expectedTime, result.timestamp)
        assertNull(result.windData.direction)
        assertNull(result.windData.speed)
        assertNull(result.windData.gust)
        assertNull(result.precipitation.rainLastHour)
        assertNull(result.precipitation.rainLast24Hours)
        assertEquals(12.scale(Hundreths).inches, result.precipitation.rainToday)
        assertNull(result.humidity)
        assertNull(result.pressure)
        assertNull(result.irradiance)
        assertNull(result.symbol)
        assertNull(result.coordinates)
    }

    @Test
    fun nonWeather() {
        val given = ">Hello World"

        assertFails { parser.parse(given) }
    }

    @Test
    fun generate() {
        val given = PacketData.Weather(
            Clock.System.now().withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 5,
                minute = 56,
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
            coordinates = null,
            symbol = null,
            temperature = 77.fahrenheit,
            humidity = 50.percent,
            pressure = 9900.scale(Deka).pascals,
            irradiance = 69.wattsPerSquareMeter,
        )

        val result = parser.generate(given)

        assertEquals("_10090556c220s004g005t077r001p002P003h50b09900L069s004#789", result)
    }

    @Test
    fun generateEmpty() {
        val given = PacketData.Weather(
            Clock.System.now().withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 5,
                minute = 56,
                second = 0,
                nanosecond = 0
            ),
            windData = WindData(null, null, null),
            precipitation = Precipitation(null, null, null),
            coordinates = null,
            symbol = null,
            temperature = null,
            humidity = null,
            pressure = null,
            irradiance = null,
        )

        val result = parser.generate(given)

        assertEquals("_10090556c...s...g...t...", result)
    }

    @Test
    fun generateSecondTierIrradiance() {
        val given = PacketData.Weather(
            Clock.System.now().withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 5,
                minute = 56,
                second = 0,
                nanosecond = 0
            ),
            windData = WindData(null, null, null),
            precipitation = Precipitation(null, null, null),
            coordinates = null,
            symbol = null,
            temperature = null,
            humidity = null,
            pressure = null,
            irradiance = 1025.wattsPerSquareMeter,
        )

        val result = parser.generate(given)

        assertEquals("_10090556c...s...g...t...l025", result)
    }
}
