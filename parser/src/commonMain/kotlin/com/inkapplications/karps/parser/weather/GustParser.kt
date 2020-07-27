package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.structures.unit.Speed
import com.inkapplications.karps.structures.unit.mph

/**
 * Parse gust speed from APRS wind speed data.
 */
object GustParser {
    val regex: Regex = Regex("""g(\d{3}|\.{3}|\s{3})""")

    fun parse(data: String): Speed? {
        val value = regex.find(data)?.groupValues?.get(1) ?: return null
        if ('.' in value || ' ' in value) return null

        return value.toShort().mph
    }
}
