package com.inkapplications.karps.parser.chunk.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SpanUntilChunkerTest {
    @Test
    fun stopAtChar() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            maxLength = 5,
            minLength = 5
        )

        val result = parser.popChunk(given)

        assertEquals("12345", result.parsed)
        assertEquals(",6789", result.remainingData)
    }

    @Test
    fun maxLengthFail() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            maxLength = 4,
            minLength = 5
        )

        assertFails("Maximum length of span enforced.") { parser.popChunk(given) }
    }

    @Test
    fun minLengthFail() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            maxLength = 5,
            minLength = 6
        )

        assertFails("Maximum length of span enforced.") { parser.popChunk(given) }
    }
}
