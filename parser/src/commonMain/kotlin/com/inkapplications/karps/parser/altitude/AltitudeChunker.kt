package com.inkapplications.karps.parser.altitude

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Length

/**
 * Parse Altitude from a packet's comment field.
 *
 * Because the comment can appear *anywhere* in the comment field, this
 * will parse and remove anything resembling an altitude, ie. anything
 * matching the pattern: `/A=[\d]{6}`.
 * This should therefore be run as late in the parsing process as possible.
 */
internal object AltitudeChunker: Chunker<Length> {
    override fun popChunk(data: String): Chunk<out Length> {
        val startIndex = data.indexOf("/A=")
        val value = data.substring(startIndex + 3, startIndex + 9)
        val altitude = Feet.of(value.toInt())

        return Chunk(altitude, data.removeRange(startIndex, startIndex + 9))
    }
}
