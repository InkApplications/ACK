package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime

/**
 * Parse Days/Hours/Minutes for UTC times.
 */
class DhmzParser: TimestampParser {
    private val pattern = Regex("""^(0[1-9]|[1-2][0-9]|3[01])([01][0-9]|2[0-4])([0-5][0-9])[Zz]$""")

    override fun parse(timestamp: String): Timestamp {
        val (days, hours, minutes) = pattern.find(timestamp)?.destructured
            ?: throw PacketFormatException("Information does not contain a DHMZ Timestamp")

        return DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = days.toInt(),
                hours = hours.toInt(),
                minutes = minutes.toInt(),
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
    }
}
