package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.unit.*
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class PositionlessWeatherParserTest {
    @Test
    fun parse() {
        val given = "10090556c220s004g005t077r001p002P003h50b09900wRSW"

        val result = PositionlessWeatherParser().parse(TestData.prototype.copy(body = given))
        val expectedTime = DateTime.now()
            .copyDayOfMonth(
                month = Month.October,
                dayOfMonth = 9,
                hours = 5,
                minutes = 56,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        assertEquals(expectedTime, result.timestamp)
        assertEquals(220.degreesBearing, result.windData.direction)
        assertEquals(4.mph, result.windData.speed)
        assertEquals(5.mph, result.windData.gust)
        assertEquals(1.hundredthsOfInch, result.precipitation.rainLastHour)
        assertEquals(2.hundredthsOfInch, result.precipitation.rainLast24Hours)
        assertEquals(3.hundredthsOfInch, result.precipitation.rainToday)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.decapascals, result.pressure)
        assertNull(result.irradiance)
        assertNull(result.symbol)
        assertNull(result.position)
    }

    @Test
    fun empty() {
        val given = "10090556c...s   g...t...P012Jim"

        val result = PositionlessWeatherParser().parse(TestData.prototype.copy(body = given))
        val expectedTime = DateTime.now()
            .copyDayOfMonth(
                month = Month.October,
                dayOfMonth = 9,
                hours = 5,
                minutes = 56,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        assertEquals(expectedTime, result.timestamp)
        assertNull(result.windData.direction)
        assertNull(result.windData.speed)
        assertNull(result.windData.gust)
        assertNull(result.precipitation.rainLastHour)
        assertNull(result.precipitation.rainLast24Hours)
        assertEquals(12.hundredthsOfInch, result.precipitation.rainToday)
        assertNull(result.humidity)
        assertNull(result.pressure)
        assertNull(result.irradiance)
        assertNull(result.symbol)
        assertNull(result.position)
    }

    @Test
    fun nonWeather() {
        val given = "Hello World"

        assertFails { PositionlessWeatherParser().parse(TestData.prototype.copy(body = given)) }
    }
}
