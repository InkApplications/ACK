package com.inkapplications.ack.parser.chunk.common

import com.inkapplications.ack.parser.SimpleCodec
import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker

/**
 * Pops a fixed length of characters off the data and decodes with a delegate codec.
 */
internal class FixedLengthChunker<T>(
    private val codec: SimpleCodec<T>,
    private val fixedLength: Int,
): Chunker<T> {
    override fun popChunk(data: String): Chunk<out T> {
        val result = codec.decode(data)

        return Chunk(result, data.substring(fixedLength))
    }
}