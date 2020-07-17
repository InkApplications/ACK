package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketFormatException
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset

/**
 * Parse Days/Hours/Minutes for local times.
 */
class DhmlParser(
    private val timezone: TimezoneOffset = TimezoneOffset.local(DateTime.now())
): TimestampParser {
    private val pattern = Regex("""^(0[1-9]|[1-2][0-9]|3[01])([01][0-9]|2[0-4])([0-5][0-9])/$""")

    override fun parse(timestamp: String): DateTime {
        val (days, hours, minutes) = pattern.find(timestamp)?.destructured
            ?: throw PacketFormatException("Information does not contain a DHML Timestamp")

        return DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = days.toInt(),
                hours = hours.toInt(),
                minutes = minutes.toInt(),
                seconds = 0
            )
            .toOffsetUnadjusted(timezone)
            .utc
    }
}
