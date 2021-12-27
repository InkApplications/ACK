package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.Chunker

/**
 * Parse a single character off of the current data.
 */
internal object CharChunker: Chunker<Char> {
    override fun popChunk(data: String): Chunk<Char> {
        return Chunk(data[0], data.substring(1))
    }
}
