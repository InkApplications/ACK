package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.structures.unit.Temperature

/**
 * Parse real temperature from APRS notation.
 */
object TemperatureParser {
    val regex: Regex = Regex("""t(\d{3}|\.{3}|\s{3})""")

    fun parse(data: String): Temperature? {
        val value = regex.find(data)?.groupValues?.get(1) ?: return null
        if ('.' in value || ' ' in value) return null

        return Temperature(value.toFloat())
    }
}
