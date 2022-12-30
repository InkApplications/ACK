package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.unit.Strength
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.roundToInt
import inkapplications.spondee.structure.scale
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalExtensionChunkerTest {
    @Test
    fun validRange() {
        val given = "DFS2364Test"

        val result = SignalExtensionChunker.popChunk(given)

        assertEquals(Strength(2), result.result.value.strength)
        assertEquals(Cardinal.South.toAngle(), result.result.value.direction)
        assertEquals(6.scale(Deci).bels, result.result.value.gain)
        assertEquals(80.feet.roundToInt(), result.result.value.height?.toFeet()?.roundToInt())
        assertEquals("Test", result.remainingData, "Parsed data is removed")
    }
}
