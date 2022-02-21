package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.SignalInfo
import com.inkapplications.ack.structures.unit.Strength
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

class SignalInfoCodecTest {
    @Test
    fun decode() {
        val given = "DFS2364"

        val result = SignalInfoCodec.decode(given)

        assertEquals(Strength(2), result.strength)
        assertEquals(Cardinal.South.toAngle(), result.direction)
        assertEquals(Bels.of(Deci, 6), result.gain)
        assertEquals(Feet.of(80), result.height)
    }

    @Test
    fun decodeOmniDirection() {
        val given = "DFS2360"

        val result = SignalInfoCodec.decode(given)

        assertEquals(Strength(2), result.strength)
        assertNull(result.direction)
        assertEquals(Bels.of(Deci, 6), result.gain)
        assertEquals(Feet.of(80), result.height)
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
            height = Feet.of(80),
            gain = Bels.of(Deci, 6),
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
