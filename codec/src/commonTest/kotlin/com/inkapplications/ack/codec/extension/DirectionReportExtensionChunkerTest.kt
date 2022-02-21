package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.unit.Knots
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.spatial.Degrees
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class DirectionReportExtensionChunkerTest {
    @Test
    fun validTrajectory() {
        val given = "088/036/270/729Hello World"

        val result = DirectionReportExtensionChunker.popChunk(given)

        assertEquals(Degrees.of(88), result.result.value.trajectory.direction)
        assertEquals(Knots.of(36), result.result.value.trajectory.speed)
        assertEquals(Degrees.of(270), result.result.value.bearing)
        assertEquals(7.toShort(), result.result.value.quality?.number)
        assertEquals(Miles.of(4), result.result.value.quality?.range)
        assertEquals(Degrees.of(1), result.result.value.quality?.accuracy)
        assertEquals("Hello World", result.remainingData)
    }

    @Test
    fun unspecifiedTrajectory() {
        val given = "   /.../270/729Hello World"

        val result = DirectionReportExtensionChunker.popChunk(given)

        assertNull(result.result.value.trajectory.direction)
        assertNull(result.result.value.trajectory.speed)
        assertEquals(Degrees.of(270), result.result.value.bearing)
        assertEquals(7.toShort(), result.result.value.quality?.number)
        assertEquals(Miles.of(4), result.result.value.quality?.range)
        assertEquals(Degrees.of(1), result.result.value.quality?.accuracy)
        assertEquals("Hello World", result.remainingData)
    }

    @Test
    fun invalidTrajectoryValues() {
        val given = "abc/def/ghi/jkl"

        assertFails("Should not parse non-numbers") { DirectionReportExtensionChunker.popChunk(given) }
    }

    @Test
    fun controlMissing() {
        val given = "012,345,678,910"

        assertFails("Should not parse if control character is missing.") { DirectionReportExtensionChunker.popChunk(given) }
    }

    @Test
    fun missingBearing() {
        val given = "088/036/.../729Hello World"

        assertFails("Bearing cannot be unspecified.") { DirectionReportExtensionChunker.popChunk(given) }
    }

    @Test
    fun missingNrq() {
        val given = "088/036/270/   Hello World"

        assertFails("NRQ cannot be unspecified.") { DirectionReportExtensionChunker.popChunk(given) }
    }
}
