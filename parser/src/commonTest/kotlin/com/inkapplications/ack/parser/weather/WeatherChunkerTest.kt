package com.inkapplications.ack.parser.weather

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class WeatherChunkerTest {
    @Test
    fun getChunks() {
        val data = "a123b4567c89012d00"
        val result = WeatherChunker.popChunk(data).result
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
        assertFails("Fails when no data is provided") { WeatherChunker.popChunk(data) }
    }

    @Test
    fun invalid() {
        val data = "hello wald!"

        assertFails("Fails when data is not weather data") { WeatherChunker.popChunk(data) }
    }

    @Test
    fun missing() {
        val data = "a123b....c12345"
        val result = WeatherChunker.popChunk(data).result
        val expected = mapOf(
            'a' to 123,
            'c' to 12345
        )

        assertEquals(expected, result)
    }

    @Test
    fun negative() {
        val data = "a-12"
        val result = WeatherChunker.popChunk(data).result
        val expected = mapOf(
            'a' to -12
        )

        assertEquals(expected, result)
    }
}
