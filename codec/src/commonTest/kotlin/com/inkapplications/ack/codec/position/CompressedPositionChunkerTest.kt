package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.valueFor
import inkapplications.spondee.measure.us.toKnots
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.structure.toDouble
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
        assertEquals(36.2, result.result.extension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)?.speed?.toKnots()?.toDouble()!!, 1e-1)
        assertEquals(88.degrees, result.result.extension.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)?.direction)
    }

    @Test
    fun invalidPosition() {
        val given = "Hello World"

        assertFails { CompressedPositionChunker.popChunk(given) }
    }
}
