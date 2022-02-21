package com.inkapplications.ack.codec.chunk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class ChunkerTest {
    @Test
    fun mapParsed() {
        val given = StubChunker.mapParsed { "$it|Mapped" }

        val result = given.popChunk("")

        assertEquals("Parsed|Mapped", result.result)
        assertEquals("Remaining", result.remainingData)
    }

    @Test
    fun parseOptionalAfterValid() {
        val result = StubChunker.parseOptionalAfter(Chunk("", ""))

        assertEquals("Parsed", result.result)
        assertEquals("Remaining", result.remainingData)
    }

    @Test
    fun parseOptionalAfterInvalid() {
        val given = object: Chunker<String> {
            override fun popChunk(data: String): Chunk<out String> = TODO()
        }
        val result = given.parseOptionalAfter(Chunk("", "test"))

        assertNull(result.result)
        assertEquals("test", result.remainingData)
    }

    @Test
    fun requireControlValid() {
        val given = 'C'

        given.requireControl('A', 'B', 'C', 'D')
    }

    @Test
    fun requireControlInvalid() {
        val given = 'F'

        assertFails { given.requireControl('A', 'B', 'C', 'D') }
    }

    @Test
    fun requireStartsWithValid() {
        val given = "Hello world"

        given.requireStartsWith("Hell")
    }

    @Test
    fun requireStartsWithInvalid() {
        val given = "Hello world"

        assertFails { given.requireStartsWith("Heaven") }
    }
}

internal object StubChunker: Chunker<String> {
    override fun popChunk(data: String): Chunk<out String> {
        return Chunk("Parsed", "Remaining")
    }
}
