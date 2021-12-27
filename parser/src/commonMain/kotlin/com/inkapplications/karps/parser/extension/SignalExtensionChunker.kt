package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.extension.DataExtensions.OmniDfSignalExtra

/**
 * Parse a signal-strength report extension.
 *
 * This follows the format `DFSphgd` and does not allow omitted values.
 */
internal object SignalExtensionChunker: Chunker<OmniDfSignalExtra> {
    override fun popChunk(data: String): Chunk<OmniDfSignalExtra> {
        val signal = SignalInfoCodec.decode(data.substring(0, 7))

        return OmniDfSignalExtra(signal)
            .let { Chunk(it, data.substring(7)) }
    }
}
