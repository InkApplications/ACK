package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk

/**
 * Parse a single character off of the current data.
 */
object CharChunker: Chunker<Char> {
    override fun popChunk(data: String): Chunk<Char> {
        return Chunk(data[0], data.substring(1))
    }
}
