package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.Chunker

/**
 * Extracts key/value data from weather data.
 *
 * This parses generic data formatted by a single character and an integer
 * representing the value for that field.
 * For example `a123b4567` will parse to: `['a': 123, 'b', 4567]`
 * Per the APRS specification, values can be between 2-5 characters, and the
 * identifier is a single character.
 */
internal object WeatherChunker: Chunker<Map<Char, Int>> {
    private const val ID = """[a-zA-Z#]"""
    private const val DATA = """(?:[\-\d]{1}\d{1,4}|[\.\s]{2,5})"""
    private val weatherDataFormat = Regex("""^((?:${ID}${DATA})+)""")
    private val chunkFormat = Regex("""(${ID})(${DATA})""")

    override fun popChunk(data: String): Chunk<out Map<Char, Int>> {
        val result = weatherDataFormat.find(data)!!
        val weatherData = chunkFormat.findAll(result.groupValues[1])

        return weatherData.map {
                it.groupValues[1].single() to it.groupValues[2].toIntOrNull()
            }
            .toMap()
            .filterValues { it != null }
            .mapValues { it.value!! }
            .let { Chunk(it, data.substring(result.groupValues[1].length)) }
    }
}
