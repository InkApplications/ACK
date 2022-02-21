package com.inkapplications.ack.codec.weather

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.chunk.requireControl
import com.inkapplications.ack.codec.optionalValue

internal class WeatherElementChunker(
    private val identifier: Char,
    private val length: Int
): Chunker<Int?> {
    override fun popChunk(data: String): Chunk<out Int?> {
        data[0].requireControl(identifier)
        return data.substring(1, length + 1)
            .optionalValue
            .let { Chunk(it, data.substring(length + 1)) }
    }
}
