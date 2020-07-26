package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.structures.unit.Bearing
import com.inkapplications.karps.structures.unit.degreesBearing

/**
 * Parse a bearing from APRS wind direction data.
 */
object WindDirectionParser {
    val regex: Regex = Regex("""c(\d{3}|\.{3}|\s{3})""")

    fun parse(data: String): Bearing? {
        val value = regex.find(data)?.groupValues?.get(1) ?: return null
        if ('.' in value || ' ' in value) return null

        return value.toShort().degreesBearing
    }
}
