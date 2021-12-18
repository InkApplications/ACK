package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.structures.unit.Strength
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
