package com.inkapplications.ack.codec.extension

import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.toDouble
import inkapplications.spondee.structure.value
import kotlin.test.Test
import kotlin.test.assertEquals

class TransmitterInfoExtensionChunkerTest {
    @Test
    fun validInfo() {
        val given = "PHG5132Test"

        val result = TransmitterInfoExtensionChunker.popChunk(given)

        assertEquals(25.0, result.result.value.power?.toWatts()!!.toDouble(), 1e-15)
        assertEquals(20.0, result.result.value.height?.toFeet()!!.toDouble(), 1e-15)
        assertEquals(3.0, result.result.value.gain?.toBels()?.value(Deci)!!.toDouble(), 1e-15)
        assertEquals(Cardinal.East.toAngle().toDegrees().toDouble(), result.result.value.direction?.toDegrees()!!.toDouble(), 1e-15)
        assertEquals("Test", result.remainingData)
    }
}
