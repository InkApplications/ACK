package com.inkapplications.ack.codec.chunk.common

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker

/**
 * Get a fixed length chunk of data.
 *
 * @param length The length of the string to get from data.
 * @param offset Fixed number of characters to be discarded at the start of the string.
 */
internal class SpanChunker(
    private val length: Int,
    private val offset: Int = 0
): Chunker<String> {
    override fun popChunk(data: String): Chunk<String> {
        return Chunk(data.substring(offset, offset + length), data.removeRange(offset, offset + length))
    }
}

