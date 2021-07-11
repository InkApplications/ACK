package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class SignalExtensionChunkerTest {
    @Test
    fun validRange() {
        val given = "DFS2364Test"

        val result = SignalExtensionChunker.popChunk(given)

        assertEquals(Strength(2), result.result.value.strength)
        assertEquals(Cardinal.South.toAngle(), result.result.value.direction)
        assertEquals(Bels.of(Deci, 6), result.result.value.gain)
        assertEquals(Feet.of(80), result.result.value.height)
        assertEquals("Test", result.remainingData, "Parsed data is removed")
    }

    @Test
    fun omniDirection() {
        val given = "DFS2360Test"

        val result = SignalExtensionChunker.popChunk(given)

        assertEquals(Strength(2), result.result.value.strength)
        assertNull(result.result.value.direction)
        assertEquals(Bels.of(Deci, 6), result.result.value.gain)
        assertEquals(Feet.of(80), result.result.value.height)
        assertEquals("Test", result.remainingData, "Parsed data is removed")
    }

    @Test
    fun illegalValue() {
        val given = "DFSHello World"

        assertFails("Should not parse illegal characters") { SignalExtensionChunker.popChunk(given) }
    }

    @Test
    fun missingControl() {
        val given = "PHG0050Test"

        assertFails("Should not parse with wrong control.") { SignalExtensionChunker.popChunk(given) }
    }
}
