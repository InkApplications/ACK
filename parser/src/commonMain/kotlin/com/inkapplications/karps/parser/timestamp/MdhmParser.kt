package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketFormatException
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month

/**
 * Parse Month/Day/Hours/Minutes format.
 */
class MdhmParser: TimestampParser {
    val regex = Regex("""^(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[01])([01][0-9]|2[0-4])([0-5][0-9])$""")

    override fun parse(timestamp: String): DateTime {
        val (month, days, hours, minutes) = regex.find(timestamp)?.destructured
            ?: throw PacketFormatException("Information does not contain a MDHM Timestamp")

        return DateTime.now()
            .copyDayOfMonth(
                month = Month.get(month.toInt()),
                dayOfMonth = days.toInt(),
                hours = hours.toInt(),
                minutes = minutes.toInt(),
                seconds = 0
            )
    }
}
