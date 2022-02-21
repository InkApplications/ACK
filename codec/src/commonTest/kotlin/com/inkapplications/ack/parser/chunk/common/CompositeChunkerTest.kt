package com.inkapplications.ack.parser.chunk.common

import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker
import kotlin.test.*

class CompositeChunkerTest {
    @Test
    fun fistReturned() {
        val first = object: Chunker<String> {
            var called = false
            override fun popChunk(data: String): Chunk<out String> {
                called = true
                TODO("Fail")
            }
        }
        val second = object: Chunker<String> {
            override fun popChunk(data: String): Chunk<out String> = Chunk("Test", data)
        }
        val third = object : Chunker<String> {
            var called = false
            override fun popChunk(data: String): Chunk<out String> {
                called = true
                TODO()
            }
        }

        val composite = CompositeChunker(first, second, third)

        val result = composite.popChunk("hello world")

        assertTrue(first.called, "Parsers are called in-order")
        assertEquals("Test", result.result, "Result from second parser returned")
        assertFalse(third.called, "Remaining parsers are not called")
    }

    @Test
    fun noMatches() {
        val fail = object: Chunker<Nothing> {
            override fun popChunk(data: String): Chunk<out Nothing> = TODO()
        }

        val composite = CompositeChunker(fail)

        assertFails("Exception is thrown when all parsers fail") { composite.popChunk("test") }
    }
}
