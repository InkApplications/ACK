package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker
import com.inkapplications.ack.parser.extension.DataExtensions.RangeExtra

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
