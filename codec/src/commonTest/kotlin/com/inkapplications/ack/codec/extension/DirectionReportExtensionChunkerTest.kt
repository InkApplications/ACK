package com.inkapplications.ack.codec.extension

import inkapplications.spondee.measure.us.toKnots
import inkapplications.spondee.measure.us.toMiles
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.toDouble
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class DirectionReportExtensionChunkerTest {
    @Test
    fun validTrajectory() {
        val given = "088/036/270/729Hello World"

        val result = DirectionReportExtensionChunker.popChunk(given)

        assertEquals(88.0, result.result.value.trajectory.direction?.toDegrees()!!.toDouble(), 1e-15)
        assertEquals(36.0, result.result.value.trajectory.speed?.toKnots()!!.toDouble(), 1e-15)
        assertEquals(270.0, result.result.value.bearing.toDegrees().toDouble(), 1e-15)
        assertEquals(7.toShort(), result.result.value.quality.number)
        assertEquals(4.0, result.result.value.quality.range.toMiles().toDouble(), 1e-15)
        assertEquals(1.0, result.result.value.quality.accuracy.toDegrees().toDouble(), 1e-15)
        assertEquals("Hello World", result.remainingData)
    }

    @Test
    fun unspecifiedTrajectory() {
        val given = "   /.../270/729Hello World"

        val result = DirectionReportExtensionChunker.popChunk(given)

        assertNull(result.result.value.trajectory.direction)
        assertNull(result.result.value.trajectory.speed)
        assertEquals(270.0, result.result.value.bearing.toDegrees().toDouble(), 1e-15)
        assertEquals(7.toShort(), result.result.value.quality.number)
        assertEquals(4.0, result.result.value.quality.range.toMiles().toDouble(), 1e-15)
        assertEquals(1.0, result.result.value.quality.accuracy.toDegrees().toDouble(), 1e-15)
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
