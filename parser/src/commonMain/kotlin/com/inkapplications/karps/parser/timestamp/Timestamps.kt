package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.structures.unit.Timestamp

/**
 * Regex format that matches all APRS timestamps.
 */
const val TIMESTAMP = """(?:[0-9]{6}[0-9z/h]|[0-9]{8})"""

/**
 * Parse an APRS timestamp into a Date/Time.
 */
interface TimestampParser {
    /**
     * Parse a date from the given timestamp.
     *
     * @param timestamp a string matching [TIMESTAMP] format to be parsed.
     * @return a UTC local date/time.
     */
    fun parse(timestamp: String): Timestamp
}
