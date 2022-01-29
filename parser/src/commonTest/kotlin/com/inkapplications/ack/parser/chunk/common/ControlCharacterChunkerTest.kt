package com.inkapplications.ack.parser.chunk.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ControlCharacterChunkerTest {
    @Test
    fun matches() {
        val given = ":test"
        val parser = ControlCharacterChunker(':')

        val result = parser.popChunk(given)

        assertEquals("test", result.remainingData)
    }

    @Test
    fun failure() {
        val given = "test"
        val parser = ControlCharacterChunker(':')

        assertFails("Exception thrown when control character is not found") { parser.popChunk(given) }
    }
}
