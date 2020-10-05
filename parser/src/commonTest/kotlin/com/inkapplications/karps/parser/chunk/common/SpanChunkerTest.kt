package com.inkapplications.karps.parser.chunk.common

import kotlin.test.Test
import kotlin.test.assertEquals

class SpanChunkerTest {
    @Test
    fun parseSpan() {
        val given = "123456789"
        val parser = SpanChunker(5)

        val result = parser.popChunk(given)

        assertEquals("12345", result.result)
        assertEquals("6789", result.remainingData)
    }

    @Test
    fun offsetSpan() {
        val given = "123456789"
        val parser = SpanChunker(5, offset = 1)

        val result = parser.popChunk(given)

        assertEquals("23456", result.result)
        assertEquals("1789", result.remainingData)
    }
}
