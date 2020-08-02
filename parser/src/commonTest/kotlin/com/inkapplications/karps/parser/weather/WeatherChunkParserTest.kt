package com.inkapplications.karps.parser.weather

import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherChunkParserTest {
    @Test
    fun getChunks() {
        val data = "a123b4567c89012d00"
        val result = WeatherChunkParser.getChunks(data)
        val expected = mapOf(
            'a' to 123,
            'b' to 4567,
            'c' to 89012,
            'd' to 0
        )

        assertEquals(expected, result)
    }

    @Test
    fun empty() {
        val data = ""
        val result = WeatherChunkParser.getChunks(data)
        val expected = emptyMap<Char, Int>()

        assertEquals(expected, result)
    }

    @Test
    fun invalid() {
        val data = "hello wald!"
        val result = WeatherChunkParser.getChunks(data)
        val expected = emptyMap<Char, Int>()

        assertEquals(expected, result)
    }

    @Test
    fun missing() {
        val data = "a123b....c12345"
        val result = WeatherChunkParser.getChunks(data)
        val expected = mapOf(
            'a' to 123,
            'c' to 12345
        )

        assertEquals(expected, result)
    }

    @Test
    fun negative() {
        val data = "a-12"
        val result = WeatherChunkParser.getChunks(data)
        val expected = mapOf(
            'a' to -12
        )

        assertEquals(expected, result)
    }
}
