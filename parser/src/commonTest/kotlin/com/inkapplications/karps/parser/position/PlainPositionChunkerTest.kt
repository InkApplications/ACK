package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.assertEquals
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.latitudeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class PlainPositionChunkerTest {
    @Test
    fun validPosition() {
        val given = "4903.50N/07201.75W-Test 001234"

        val result = PlainPositionChunker.popChunk(given)
        val expected = latitudeOf(49, 3, 30f, Cardinal.North)

        assertEquals(expected, result.result.coordinates.latitude, 1e-8)
    }

    @Test
    fun invalidPosition() {
        val given = "Hello World"

        assertFails { PlainPositionChunker.popChunk(given) }
    }
}
