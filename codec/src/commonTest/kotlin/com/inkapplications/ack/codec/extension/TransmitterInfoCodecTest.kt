package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.TransmitterInfo
import inkapplications.spondee.measure.metric.watts
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.scale
import inkapplications.spondee.structure.toDouble
import inkapplications.spondee.structure.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TransmitterInfoCodecTest {
    @Test
    fun validInfo() {
        val given = "PHG5132"

        val result = TransmitterInfoCodec.decode(given)

        assertEquals(25.0, result.power?.toWatts()!!.toDouble(), 1e-15)
        assertEquals(20.0, result.height?.toFeet()!!.toDouble(), 1e-15)
        assertEquals(3.0, result.gain?.toBels()!!.value(Deci).toDouble(), 1e-15)
        assertEquals(Cardinal.East.toAngle().toDegrees().toDouble(), result.direction?.toDegrees()!!.toDouble(), 1e-15)
    }

    @Test
    fun invalidInfo() {
        val given = "PHGHello World"

        assertFails("Should not parse non-numbers") { TransmitterInfoCodec.decode(given) }
    }

    @Test
    fun missingControl() {
        val given = "RNG1234"

        assertFails("Should not parse if control is wrong") { TransmitterInfoCodec.decode(given) }
    }

    @Test
    fun encode() {
        val given = TransmitterInfo(
            power = 25.watts,
            height = 20.feet,
            gain = 3.scale(Deci).bels,
            direction = Cardinal.East.toAngle(),
        )

        val result = TransmitterInfoCodec.encode(given)

        assertEquals("PHG5132", result)
    }
}
