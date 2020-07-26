package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.structures.unit.Speed
import com.inkapplications.karps.structures.unit.mph

/**
 * Parse speed from APRS wind speed data.
 */
object WindSpeedParser {
    val regex: Regex = Regex("""s(\d{3}|\.{3}|\s{3})""")

    fun parse(data: String): Speed? {
        val value = regex.find(data)?.groupValues?.get(1) ?: return null
        if ('.' in value || ' ' in value) return null

        return value.toShort().mph
    }
}
