package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireStartsWith
import com.inkapplications.karps.parser.digit
import com.inkapplications.karps.parser.extension.DataExtensions.OmniDfSignalExtra
import com.inkapplications.karps.structures.SignalInfo
import com.inkapplications.karps.structures.unit.decibels
import com.inkapplications.karps.structures.unit.feet
import com.inkapplications.karps.structures.unit.strength
import inkapplications.spondee.spatial.Degrees
import kotlin.math.pow

/**
 * Parse a signal-strength report extension.
 *
 * This follows the format `DFSphgd` and does not allow omitted values.
 */
internal object SignalExtensionChunker: Chunker<OmniDfSignalExtra> {
    override fun popChunk(data: String): Chunk<OmniDfSignalExtra> {
        data.requireStartsWith("DFS")

        val signal = SignalInfo(
            strength = data[3].digit.strength,
            height = 2.0.pow(data[4].digit.toInt()).times(10).feet,
            gain = data[5].digit.decibels,
            direction = data[6].digit.times(45).takeIf { it != 0 }?.let(Degrees::of)
        )

        return OmniDfSignalExtra(signal)
            .let { Chunk(it, data.substring(7)) }
    }
}
