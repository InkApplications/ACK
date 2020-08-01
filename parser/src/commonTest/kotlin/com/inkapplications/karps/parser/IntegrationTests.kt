package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IntegrationTests {
    @Test
    fun parsePosition() {
        val parser = ParserModule().defaultParser()

        val result = parser.fromString(TestData.Position.string)
        val expected = TestData.Position.expected

        assertBaseEquals(expected, result)
        assertType(AprsPacket.Position::class, result) {
            assertEquals(expected.coordinates.latitude.decimal, coordinates.latitude.decimal, 0.000001)
            assertEquals(expected.coordinates.longitude.decimal, coordinates.longitude.decimal, 0.000001)
            assertEquals(expected.symbol, symbol)
            assertEquals(expected.comment, comment)
            assertNull(timestamp)
        }
    }

    @Test
    fun parseCompressedPosition() {
        val parser = ParserModule().defaultParser()

        val result = parser.fromString(TestData.CompressedPosition.string)
        val expected = TestData.CompressedPosition.expected

        assertBaseEquals(expected, result)
        assertType(AprsPacket.Position::class, result) {
            assertEquals(expected.coordinates.latitude.decimal, coordinates.latitude.decimal, 0.000001)
            assertEquals(expected.coordinates.longitude.decimal, coordinates.longitude.decimal, 0.000001)
            assertEquals(expected.symbol, symbol)
            assertEquals(expected.comment, comment)
            assertNull(timestamp)
        }
    }

    @Test
    fun parsePositionlessWeather() {
        val parser = ParserModule().defaultParser()

        val result = parser.fromString(TestData.PositionlessWeather.string)
        val expected = TestData.PositionlessWeather.expected

        assertBaseEquals(expected, result)
        assertType(AprsPacket.Weather::class, result) {
            assertEquals(expected.temperature, temperature)
            assertEquals(expected.windData, windData)
            assertEquals(expected.rainData, rainData)
            assertEquals(expected.humidity, humidity)
        }
    }

    private fun assertBaseEquals(expected: AprsPacket, actual: AprsPacket) {
        assertEquals(expected.dataTypeIdentifier, actual.dataTypeIdentifier)
        assertEquals(expected.source, actual.source)
        assertEquals(expected.destination, actual.destination)
        assertEquals(expected.digipeaters, actual.digipeaters)
    }
}
