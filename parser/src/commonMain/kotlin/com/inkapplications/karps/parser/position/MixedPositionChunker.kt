package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.Chunker

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
