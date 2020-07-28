package com.inkapplications.karps.parser.weather

/**
 * Extracts key/value data from weather data.
 *
 * This parses generic data formatted by a single character and an integer
 * representing the value for that field.
 * For example `a123b4567` will parse to: `['a': 123, 'b', 4567]`
 * Per the APRS specification, values can be between 2-5 characters, and the
 * identifier is a single character.
 */
object WeatherChunkParser {
    private val pattern = Regex("""([a-zA-Z])(\d{2,5}|\.{2,5})""")

    fun getChunks(data: String): Map<Char, Int> {
        return pattern.findAll(data)
            .map {
                it.groupValues[1].single() to it.groupValues[2].toIntOrNull()
            }
            .toMap()
            .filterValues { it != null }
            .mapValues { it.value!! }
    }
}
