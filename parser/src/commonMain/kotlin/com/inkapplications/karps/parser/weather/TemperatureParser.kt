package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.structures.Degrees

/**
 * Parse real temperature from APRS notation.
 */
object TemperatureParser {
    val regex: Regex = Regex("""t(\d{3}|\.{3}|\s{3})""")

    fun parse(data: String): Degrees? {
        val value = regex.find(data)?.groupValues?.get(1) ?: return null
        if ('.' in value || ' ' in value) return null

        return Degrees(value.toFloat())
    }
}
