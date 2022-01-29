package com.inkapplications.ack.parser.chunk.common

import com.inkapplications.ack.parser.SimpleCodec
import kotlin.test.Test
import kotlin.test.assertEquals

class FixedLengthChunkerTest {
    @Test
    fun pop() {
        val given = "123456Test"
        val codec = object: SimpleCodec<String> {
            override fun encode(data: String): String = TODO()
            override fun decode(data: String): String = "decoded"
        }

        val result = FixedLengthChunker(codec, 6).popChunk(given)

        assertEquals("decoded", result.result)
        assertEquals("Test", result.remainingData)
    }
}
