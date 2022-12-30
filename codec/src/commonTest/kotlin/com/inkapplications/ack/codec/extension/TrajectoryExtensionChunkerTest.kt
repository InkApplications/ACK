package com.inkapplications.ack.codec.extension

import inkapplications.spondee.measure.us.knots
import inkapplications.spondee.spatial.degrees
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class TrajectoryExtensionChunkerTest {
    @Test
    fun validTrajectory() {
        val given = "088/036Hello World"

        val result = TrajectoryExtensionChunker.popChunk(given)

        assertEquals(88.degrees, result.result.value.direction)
        assertEquals(36.knots, result.result.value.speed)
        assertEquals("Hello World", result.remainingData)
    }

    @Test
    fun unspecifiedTrajectory() {
        val given = "   /...Hello World"

        val result = TrajectoryExtensionChunker.popChunk(given)

        assertNull(result.result.value.direction, "Direction null when unspecified.")
        assertNull(result.result.value.speed, "Speed null when unspecified.")
        assertEquals("Hello World", result.remainingData)
    }

    @Test
    fun invalidTrajectoryValues() {
        val given = "hel/lo world"

        assertFails("Should not parse non-numbers") { TrajectoryExtensionChunker.popChunk(given) }
    }

    @Test
    fun controlMissing() {
        val given = "012|345Hello World"

        assertFails("Should not parse if control character is missing") { TrajectoryExtensionChunker.popChunk(given) }
    }
}
