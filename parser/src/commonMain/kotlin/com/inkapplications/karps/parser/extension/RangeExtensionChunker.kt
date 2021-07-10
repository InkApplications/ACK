package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireStartsWith
import com.inkapplications.karps.parser.extension.DataExtensions.RangeExtra
import inkapplications.spondee.measure.Miles

/**
 * Parse the transmitting range of a station via extension.
 *
 * This follows the format `RNGphgd` and does not allow omitted values.
 */
internal object RangeExtensionChunker: Chunker<RangeExtra> {
    override fun popChunk(data: String): Chunk<RangeExtra> {
        data.requireStartsWith("RNG")

        return Miles.of(data.substring(3, 7).toInt())
            .let(::RangeExtra)
            .let { Chunk(it, data.substring(7)) }
    }
}
