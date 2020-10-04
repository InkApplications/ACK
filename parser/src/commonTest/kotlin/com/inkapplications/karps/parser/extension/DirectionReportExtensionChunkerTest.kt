package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.structures.unit.degreesBearing
import com.inkapplications.karps.structures.unit.knots
import com.inkapplications.karps.structures.unit.miles
import kotlin.test.*

class DirectionReportExtensionChunkerTest {
    @Test
    fun validTrajectory() {
        val given = "088/036/270/729Hello World"

        val result = DirectionReportExtensionChunker.popChunk(given)

        assertEquals(88.degreesBearing, result.parsed.value.trajectory.direction)
        assertEquals(36.knots, result.parsed.value.trajectory.speed)
        assertEquals(270.degreesBearing, result.parsed.value.bearing)
        assertEquals(7.toShort(), result.parsed.value.quality?.number)
        assertEquals(4.miles, result.parsed.value.quality?.range)
        assertEquals(1.degreesBearing, result.parsed.value.quality?.accuracy)
        assertEquals("Hello World", result.remainingData)
    }

    @Test
    fun unspecifiedTrajectory() {
        val given = "   /.../270/729Hello World"

        val result = DirectionReportExtensionChunker.popChunk(given)

        assertNull(result.parsed.value.trajectory.direction)
        assertNull(result.parsed.value.trajectory.speed)
        assertEquals(270.degreesBearing, result.parsed.value.bearing)
        assertEquals(7.toShort(), result.parsed.value.quality?.number)
        assertEquals(4.miles, result.parsed.value.quality?.range)
        assertEquals(1.degreesBearing, result.parsed.value.quality?.accuracy)
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
