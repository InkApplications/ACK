package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk

/**
 * Parse using the first of a set of parsers to match a result.
 */
internal class CompositeChunker<out T>(
    private vararg val delegates: Chunker<T>
): Chunker<T> {
    override fun popChunk(data: String): Chunk<out T> {
        delegates.map { parser ->
            runCatching { parser.popChunk(data) }.onSuccess {
                return it
            }
        }

        throw PacketFormatException("No Matching Delegate")
    }
}
