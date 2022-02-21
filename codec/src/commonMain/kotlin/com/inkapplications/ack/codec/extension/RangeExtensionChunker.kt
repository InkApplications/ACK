package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.extension.DataExtensions.RangeExtra

/**
 * Parse the transmitting range of a station via extension.
 *
 * This follows the format `RNGphgd` and does not allow omitted values.
 */
internal object RangeExtensionChunker: Chunker<RangeExtra> {
    override fun popChunk(data: String): Chunk<RangeExtra> {
        return RangeCodec.decode(data)
            .let(::RangeExtra)
            .let { Chunk(it, data.substring(7)) }
    }
}
