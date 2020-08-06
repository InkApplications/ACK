package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime

/**
 * Parse Hours/Minutes/Seconds.
 */
class HmsParser: TimestampParser {
    private val pattern = Regex("""^([01][0-9]|2[0-4])([0-5][0-9])([0-5][0-9])[Hh]$""")

    override fun parse(timestamp: String): Timestamp {
        val (hours, minutes, seconds) = pattern.find(timestamp)?.destructured
            ?: throw PacketFormatException("Information does not contain a HMS Timestamp")

        return DateTime.now()
            .copyDayOfMonth(
                hours = hours.toInt(),
                minutes = minutes.toInt(),
                seconds = seconds.toInt(),
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
    }
}
