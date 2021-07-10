package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.parser.timestamp.withUtcValues
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.Fahrenheit
import inkapplications.spondee.measure.HundredthInches
import inkapplications.spondee.spatial.Degrees
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class WeatherParserTest {
    @Test
    fun plainComplete() {
        val given = "092345z4903.50N/07201.75W_220/004g005t-07r001p002P003h50b09900wRSW"

        val expectedTime = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = WeatherParser().parse(TestData.prototype.copy(body = given))

        assertEquals(expectedTime, result.timestamp)
        assertEquals(49.0583, result.coordinates?.latitude?.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates?.longitude?.asDecimal, 0.0001)
        assertEquals(Degrees.of(220), result.windData.direction)
        assertEquals(4.knots, result.windData.speed)
        assertEquals(5.mph, result.windData.gust)
        assertEquals(Fahrenheit.of((-7)), result.temperature)
        assertEquals(HundredthInches.of(3), result.precipitation.rainToday)
        assertEquals(HundredthInches.of(2), result.precipitation.rainLast24Hours)
        assertEquals(HundredthInches.of(1), result.precipitation.rainLastHour)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.decapascals, result.pressure)
        assertEquals(symbolOf('/', '_'), result.symbol)
        assertNull(result.irradiance)
    }

    @Test
    fun plainMinimum() {
        val given = "4903.50N/07201.75W_220/004g...t...r...p...P...h..b.....wRSW"

        val result = WeatherParser().parse(TestData.prototype.copy(body = given))

        assertNull(result.timestamp)
        assertEquals(49.0583, result.coordinates?.latitude?.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates?.longitude?.asDecimal, 0.0001)
        assertEquals(Degrees.of(220), result.windData.direction)
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
        val given = "4903.50N/07201.75W#225/000Hello World!"

        assertFails { WeatherParser().parse(TestData.prototype.copy(body = given)) }
    }

    @Test
    fun compressedComplete() {
        val given = "092345z/5L!!<*e7_7P[g005t077r001p002P003h50b09900wRSW"

        val expectedTime = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = WeatherParser().parse(TestData.prototype.copy(body = given))

        assertEquals(expectedTime, result.timestamp)
        assertEquals(49.5, result.coordinates?.latitude?.asDecimal, 0.1)
        assertEquals(-72.75, result.coordinates?.longitude?.asDecimal, 0.1)
        assertEquals(Degrees.of(88), result.windData.direction)
        assertEquals(36.2.knots, result.windData.speed)
        assertEquals(5.mph, result.windData.gust)
        assertEquals(Fahrenheit.of((77)), result.temperature)
        assertEquals(HundredthInches.of(3), result.precipitation.rainToday)
        assertEquals(HundredthInches.of(2), result.precipitation.rainLast24Hours)
        assertEquals(HundredthInches.of(1), result.precipitation.rainLastHour)
        assertEquals(50.percent, result.humidity)
        assertEquals(9900.decapascals, result.pressure)
        assertEquals(symbolOf('/', '_'), result.symbol)
        assertNull(result.irradiance)
    }

    @Test
    fun compressedEmpty() {
        val given = "/5L!!<*e7_ sTg...t...r...p...P...h..b     wRSW"

        val result = WeatherParser().parse(TestData.prototype.copy(body = given))

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
        val given = "/5L!!<*e7_ sTHello World!"

        assertFails { WeatherParser().parse(TestData.prototype.copy(body = given)) }
    }
}
