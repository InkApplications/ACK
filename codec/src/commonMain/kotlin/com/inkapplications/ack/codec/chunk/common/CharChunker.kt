package com.inkapplications.ack.codec.chunk.common

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker

/**
 * Parse a single character off of the current data.
 */
internal object CharChunker: Chunker<Char> {
    override fun popChunk(data: String): Chunk<Char> {
        return Chunk(data[0], data.substring(1))
    }
}
