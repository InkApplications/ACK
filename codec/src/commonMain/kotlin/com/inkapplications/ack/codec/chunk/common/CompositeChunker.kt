package com.inkapplications.ack.codec.chunk.common

import com.inkapplications.ack.codec.PacketFormatException
import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker

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
