package com.inkapplications.ack.codec.extension

import inkapplications.spondee.measure.Miles
import kotlin.test.Test
import kotlin.test.assertEquals

class RangeExtensionChunkerTest {
    @Test
    fun validRange() {
        val given = "RNG0050Test"

        val result = RangeExtensionChunker.popChunk(given)

        assertEquals(Miles.of(50), result.result.value, "Range is parsed as miles.")
        assertEquals("Test", result.remainingData, "Parsed data is removed")
    }
}
