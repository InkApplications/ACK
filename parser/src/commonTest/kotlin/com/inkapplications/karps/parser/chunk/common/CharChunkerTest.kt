package com.inkapplications.karps.parser.chunk.common

import kotlin.test.Test
import kotlin.test.assertEquals

class CharChunkerTest {
    @Test
    fun parseChar() {
        val given = "12345"

        val result = CharChunker.popChunk(given)

        assertEquals('1', result.parsed)
        assertEquals("2345", result.remainingData)
    }
}
