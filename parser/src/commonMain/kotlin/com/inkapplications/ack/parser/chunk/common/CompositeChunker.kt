package com.inkapplications.ack.parser.chunk.common

import com.inkapplications.ack.parser.PacketFormatException
import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker

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
