package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.structures.TransmitterInfo
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Watts
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TransmitterInfoCodecTest {
    @Test
    fun validInfo() {
        val given = "PHG5132"

        val result = TransmitterInfoCodec.decode(given)

        assertEquals(Watts.of(25), result.power)
        assertEquals(Feet.of(20), result.height)
        assertEquals(Bels.of(Deci, 3), result.gain)
        assertEquals(Cardinal.East.toAngle(), result.direction)
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
            power = Watts.of(25),
            height = Feet.of(20),
            gain = Bels.of(Deci, 3),
            direction = Cardinal.East.toAngle(),
        )

        val result = TransmitterInfoCodec.encode(given)

        assertEquals("PHG5132", result)
    }
}
