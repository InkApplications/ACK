package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.unit.Cardinal
import com.inkapplications.karps.structures.unit.Latitude
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class PlainPositionChunkerTest {
    @Test
    fun validPosition() {
        val given = "4903.50N/07201.75W-Test 001234"

        val result = PlainPositionChunker.popChunk(given)

        assertEquals(Latitude(
            degrees = 49,
            minutes = 3,
            seconds = 30f,
            cardinal = Cardinal.North
        ), result.parsed.coordinates.latitude)
    }

    @Test
    fun invalidPosition() {
        val given = "Hello World"

        assertFails { PlainPositionChunker.popChunk(given) }
    }
}
