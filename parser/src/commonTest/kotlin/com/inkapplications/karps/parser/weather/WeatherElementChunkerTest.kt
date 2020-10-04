package com.inkapplications.karps.parser.weather

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class WeatherElementChunkerTest {
    @Test
    fun getElement() {
        val given = "c220Test"

        val result = WeatherElementChunker('c', 3).popChunk(given)

        assertEquals(220, result.parsed)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun nonNumber() {
        val given = "cdefghij"

        assertFails { WeatherElementChunker('c', 3).popChunk(given) }
    }

    @Test
    fun wrongControl() {
        val given = "d220Test"

        assertFails { WeatherElementChunker('c', 3).popChunk(given) }
    }
}
