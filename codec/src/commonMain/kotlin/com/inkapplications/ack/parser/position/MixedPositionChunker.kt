package com.inkapplications.ack.parser.position

import com.inkapplications.ack.parser.PacketFormatException
import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker

internal object MixedPositionChunker: Chunker<PositionReport> {
    override fun popChunk(data: String): Chunk<out PositionReport> {
        runCatching {
            return PlainPositionChunker.popChunk(data)
        }
        runCatching {
            return CompressedPositionChunker.popChunk(data)
        }
        throw PacketFormatException("Unable to parse plain or compressed position.")
    }
}
