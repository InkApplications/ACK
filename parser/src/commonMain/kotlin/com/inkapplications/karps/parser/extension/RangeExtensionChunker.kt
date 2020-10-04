package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireStartsWith
import com.inkapplications.karps.parser.extension.DataExtensions.RangeExtra
import com.inkapplications.karps.structures.unit.miles

/**
 * Parse the transmitting range of a station via extension.
 *
 * This follows the format `RNGphgd` and does not allow omitted values.
 */
object RangeExtensionChunker: Chunker<RangeExtra> {
    override fun popChunk(data: String): Chunk<RangeExtra> {
        data.requireStartsWith("RNG")

        return data.substring(3, 7).toInt().miles
            .let(::RangeExtra)
            .let { Chunk(it, data.substring(7)) }
    }
}
