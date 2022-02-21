package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.extension.DataExtensions.OmniDfSignalExtra

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
