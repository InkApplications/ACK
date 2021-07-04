package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireStartsWith
import com.inkapplications.karps.parser.digit
import com.inkapplications.karps.parser.digitBasedValue
import com.inkapplications.karps.parser.extension.DataExtensions.TransmitterInfoExtra
import com.inkapplications.karps.structures.TransmitterInfo
import com.inkapplications.karps.structures.unit.decibels
import com.inkapplications.karps.structures.unit.feet
import com.inkapplications.karps.structures.unit.watts
import inkapplications.spondee.spatial.Degrees
import kotlin.math.pow

/**
 * Parse information about the transmitting station via extension.
 *
 * This follows the format `PHGphgd` and does not allow omitted values.
 */
internal object TransmitterInfoExtensionChunker: Chunker<TransmitterInfoExtra> {
    override fun popChunk(data: String): Chunk<TransmitterInfoExtra> {
        data.requireStartsWith("PHG")

        val power = data[3].digit.toFloat().pow(2).watts
        val height = 2.0.pow(data[4].digitBasedValue.toInt()).times(10).feet
        val gain = data[5].digit.toInt().decibels
        val direction = data[6].digit.toInt().times(45).takeIf { it != 0 }?.let(Degrees::of)

        return TransmitterInfo(power, height, gain, direction)
            .let(::TransmitterInfoExtra)
            .let { Chunk(it, data.substring(7)) }

    }
}
