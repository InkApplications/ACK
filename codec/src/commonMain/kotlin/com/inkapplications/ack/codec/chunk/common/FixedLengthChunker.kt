package com.inkapplications.ack.codec.chunk.common

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker

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
