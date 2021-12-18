package com.inkapplications.karps.parser.extension

import inkapplications.spondee.measure.Miles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class RangeCodecTest {
    @Test
    fun validRange() {
        val given = "RNG0050"

        val result = RangeCodec.decode(given)

        assertEquals(Miles.of(50), result, "Range is parsed as miles.")
    }

    @Test
    fun illegalValue() {
        val given = "RNGasdf"

        assertFails("Should not parse illegal characters") { RangeCodec.decode(given) }
    }

    @Test
    fun missingControl() {
        val given = "PHG0050"

        assertFails("Should not parse with wrong control.") { RangeCodec.decode(given) }
    }

    @Test
    fun encode() {
        val given = Miles.of(50)

        val result = RangeCodec.encode(given)

        assertEquals("RNG0050", result)
    }
}
