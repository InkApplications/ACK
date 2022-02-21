package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.extension.DataExtensions.TransmitterInfoExtra

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
