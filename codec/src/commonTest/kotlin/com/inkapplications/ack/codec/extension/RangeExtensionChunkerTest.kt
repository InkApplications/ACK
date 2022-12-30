package com.inkapplications.ack.codec.extension

import inkapplications.spondee.measure.us.miles
import kotlin.test.Test
import kotlin.test.assertEquals

class RangeExtensionChunkerTest {
    @Test
    fun validRange() {
        val given = "RNG0050Test"

        val result = RangeExtensionChunker.popChunk(given)

        assertEquals(50.miles, result.result.value, "Range is parsed as miles.")
        assertEquals("Test", result.remainingData, "Parsed data is removed")
    }
}
