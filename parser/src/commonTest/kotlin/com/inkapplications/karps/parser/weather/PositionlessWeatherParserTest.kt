package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.timestamp.withUtcValues
import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.HundredthInches
import inkapplications.spondee.spatial.Degrees
import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class PositionlessWeatherParserTest {
    @Test
    fun parse() {
        val given = "10090556c220s004g005t077r001p002P003h50b09900wRSW"

        val result = PositionlessWeatherParser().parse(TestData.prototype.copy(body = given))
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
        assertEquals(Degrees.of(220), result.windData.direction)
        assertEquals(4.mph, result.windData.speed)
        assertEquals(5.mph, result.windData.gust)
        assertEquals(HundredthInches.of(1), result.precipitation.rainLastHour)
        assertEquals(HundredthInches.of(2), result.precipitation.rainLast24Hours)
        assertEquals(HundredthInches.of(3), result.precipitation.rainToday)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.decapascals, result.pressure)
        assertNull(result.irradiance)
        assertNull(result.symbol)
        assertNull(result.coordinates)
    }

    @Test
    fun empty() {
        val given = "10090556c...s   g...t...P012Jim"

        val result = PositionlessWeatherParser().parse(TestData.prototype.copy(body = given))
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
        assertEquals(HundredthInches.of(12), result.precipitation.rainToday)
        assertNull(result.humidity)
        assertNull(result.pressure)
        assertNull(result.irradiance)
        assertNull(result.symbol)
        assertNull(result.coordinates)
    }

    @Test
    fun nonWeather() {
        val given = "Hello World"

        assertFails { PositionlessWeatherParser().parse(TestData.prototype.copy(body = given)) }
    }
}
