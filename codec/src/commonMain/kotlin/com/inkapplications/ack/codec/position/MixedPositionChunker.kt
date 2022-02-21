package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.PacketFormatException
import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker

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
