package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk

/**
 * Get a fixed length chunk of data.
 *
 * @param length The length of the string to get from data.
 * @param offset Fixed number of characters to be discarded at the start of the string.
 */
class SpanChunker(
    private val length: Int,
    private val offset: Int = 0
): Chunker<String> {
    override fun popChunk(data: String): Chunk<String> {
        return Chunk(data.substring(offset, offset + length), data.removeRange(offset, offset + length))
    }
}

