package com.inkapplications.ack.codec.chunk.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SpanUntilChunkerTest {
    @Test
    fun stopAtChar() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
        )

        val result = parser.popChunk(given)

        assertEquals("12345", result.result)
        assertEquals(",6789", result.remainingData)
    }

    @Test
    fun optional() {
        val given = "12345"
        val parser = SpanUntilChunker(
            charArrayOf(','),
        )

        val result = parser.popChunk(given)

        assertEquals("12345", result.result)
        assertEquals("", result.remainingData)
    }

    @Test
    fun required() {
        val given = "12345"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            required = true,
        )

        assertFails("Required char was missing") { parser.popChunk(given) }
    }

    @Test
    fun exactLength() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            minLength = 5,
            maxLength = 5,
            required = true,
        )

        val result = parser.popChunk(given)

        assertEquals("12345", result.result)
        assertEquals(",6789", result.remainingData)
    }

    @Test
    fun minLength() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            minLength = 6,
        )

        val result = parser.popChunk(given)

        assertEquals("12345,6789", result.result)
        assertEquals("", result.remainingData)
    }

    @Test
    fun maxLength() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            maxLength = 4,
        )

        val result = parser.popChunk(given)

        assertEquals("1234", result.result)
        assertEquals("5,6789", result.remainingData)
    }

    @Test
    fun maxLengthFail() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            maxLength = 4,
            required = true,
        )

        assertFails("Maximum length of span enforced.") { parser.popChunk(given) }
    }

    @Test
    fun minLengthFail() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            minLength = 6,
            required = true,
        )

        assertFails("Minimum length of span enforced.") { parser.popChunk(given) }
    }

    @Test
    fun popControl() {
        val given = "12345,6789"
        val parser = SpanUntilChunker(
            charArrayOf(','),
            popControlCharacter = true
        )

        val result = parser.popChunk(given)

        assertEquals("12345", result.result)
        assertEquals("6789", result.remainingData)
    }
}
