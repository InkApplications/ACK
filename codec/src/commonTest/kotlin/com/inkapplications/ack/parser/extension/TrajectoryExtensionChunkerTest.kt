package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.structures.unit.Knots
import inkapplications.spondee.spatial.Degrees
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class TrajectoryExtensionChunkerTest {
    @Test
    fun validTrajectory() {
        val given = "088/036Hello World"

        val result = TrajectoryExtensionChunker.popChunk(given)

        assertEquals(Degrees.of(88), result.result.value.direction)
        assertEquals(Knots.of(36), result.result.value.speed)
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
