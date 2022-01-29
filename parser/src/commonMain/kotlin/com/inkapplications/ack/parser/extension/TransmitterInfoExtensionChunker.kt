package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker
import com.inkapplications.ack.parser.extension.DataExtensions.TransmitterInfoExtra

/**
 * Parse information about the transmitting station via extension.
 *
 * This follows the format `PHGphgd` and does not allow omitted values.
 */
internal object TransmitterInfoExtensionChunker: Chunker<TransmitterInfoExtra> {
    override fun popChunk(data: String): Chunk<TransmitterInfoExtra> {
        return TransmitterInfoCodec.decode(data.substring(0, 7))
            .let(::TransmitterInfoExtra)
            .let { Chunk(it, data.substring(7)) }

    }
}
