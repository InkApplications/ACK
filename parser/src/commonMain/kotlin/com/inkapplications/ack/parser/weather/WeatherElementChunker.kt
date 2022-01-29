package com.inkapplications.ack.parser.weather

import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker
import com.inkapplications.ack.parser.chunk.requireControl
import com.inkapplications.ack.parser.optionalValue

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
