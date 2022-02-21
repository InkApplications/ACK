package com.inkapplications.ack.codec.chunk.common

import kotlin.test.Test
import kotlin.test.assertEquals

class CharChunkerTest {
    @Test
    fun parseChar() {
        val given = "12345"

        val result = CharChunker.popChunk(given)

        assertEquals('1', result.result)
        assertEquals("2345", result.remainingData)
    }
}
