package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.unit.Knots
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class CompressedPositionChunkerTest {
    @Test
    fun validPosition() {
        val given = "/5L!!<*e7>7P[Test"

        val result = CompressedPositionChunker.popChunk(given)

        assertEquals("Test", result.remainingData)
        assertEquals(49.5, result.result.coordinates.latitude.asDecimal, 0.01)
        assertEquals(-72.75, result.result.coordinates.longitude.asDecimal, 0.01)
        assertEquals(36.2, result.result.extension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)?.speed?.value(Knots)!!, 1e-1)
        assertEquals(Degrees.of(88), result.result.extension.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)?.direction)
    }

    @Test
    fun invalidPosition() {
        val given = "Hello World"

        assertFails { CompressedPositionChunker.popChunk(given) }
    }
}
