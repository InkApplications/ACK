package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TransmitterInfoExtensionChunkerTest {
    @Test
    fun validInfo() {
        val given = "PHG5132Test"

        val result = TransmitterInfoExtensionChunker.popChunk(given)

        assertEquals(25.watts, result.result.value.power)
        assertEquals(Feet.of(20), result.result.value.height)
        assertEquals(3.decibels, result.result.value.gain)
        assertEquals(Cardinal.East.toAngle(), result.result.value.direction)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalidInfo() {
        val given = "PHGHello World"

        assertFails("Should not parse non-numbers") { TransmitterInfoExtensionChunker.popChunk(given) }
    }

    @Test
    fun missingControl() {
        val given = "RNG1234Test"

        assertFails("Should not parse if control is wrong") { TransmitterInfoExtensionChunker.popChunk(given) }
    }
}
