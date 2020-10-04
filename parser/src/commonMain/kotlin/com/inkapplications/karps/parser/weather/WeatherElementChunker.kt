package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import com.inkapplications.karps.parser.optionalValue

class WeatherElementChunker(
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
