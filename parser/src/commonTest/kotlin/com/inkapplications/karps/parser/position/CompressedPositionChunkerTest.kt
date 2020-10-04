package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.unit.degreesBearing
import com.inkapplications.karps.structures.unit.knots
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class CompressedPositionChunkerTest {
    @Test
    fun validPosition() {
        val given = "/5L!!<*e7>7P[Test"

        val result = CompressedPositionChunker.popChunk(given)

        assertEquals("Test", result.remainingData)
        assertEquals(49.5, result.parsed.coordinates.latitude.decimal, 0.01)
        assertEquals(-72.75, result.parsed.coordinates.longitude.decimal, 0.01)
        assertEquals(36.2.knots, result.parsed.extension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)?.speed)
        assertEquals(88.degreesBearing, result.parsed.extension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)?.direction)
    }

    @Test
    fun invalidPosition() {
        val given = "Hello World"

        assertFails { CompressedPositionChunker.popChunk(given) }
    }
}
