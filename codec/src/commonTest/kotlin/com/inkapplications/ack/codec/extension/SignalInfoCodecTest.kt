package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.SignalInfo
import com.inkapplications.ack.structures.unit.Strength
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.scale
import inkapplications.spondee.structure.toDouble
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class SignalInfoCodecTest {
    @Test
    fun decode() {
        val given = "DFS2364"

        val result = SignalInfoCodec.decode(given)

        assertEquals(Strength(2), result.strength)
        assertEquals(Cardinal.South.toAngle(), result.direction)
        assertEquals(6.scale(Deci).bels, result.gain)
        assertEquals(80.0, result.height?.toFeet()!!.toDouble(), 1e-15)
    }

    @Test
    fun decodeOmniDirection() {
        val given = "DFS2360"

        val result = SignalInfoCodec.decode(given)

        assertEquals(Strength(2), result.strength)
        assertNull(result.direction)
        assertEquals(6.scale(Deci).bels, result.gain)
        assertEquals(80.0, result.height?.toFeet()!!.toDouble(), 1e-15)
    }

    @Test
    fun illegalDecode() {
        val given = "DFSABCD"

        assertFails("Should not parse illegal characters") { SignalInfoCodec.decode(given) }
    }

    @Test
    fun illegalPrefix() {
        val given = "PHG0050"

        assertFails("Should not parse with wrong control.") { SignalInfoCodec.decode(given) }
    }

    @Test
    fun decodeLengthIllegal() {
        val given = "DFS2360asdf"

        assertFails("Should not parse excess data.") { SignalInfoCodec.decode(given) }
    }

    @Test
    fun encode() {
        val given = SignalInfo(
            strength = Strength(2),
            direction = Cardinal.South.toAngle(),
            height = 80.feet,
            gain = 6.scale(Deci).bels,
        )

        val result = SignalInfoCodec.encode(given)

        assertEquals("DFS2364", result)
    }

    @Test
    fun encodeNull() {
        val given = SignalInfo(
            strength = null,
            direction = null,
            height = null,
            gain = null,
        )

        val result = SignalInfoCodec.encode(given)

        assertEquals("DFS0000", result)
    }
}
