package com.inkapplications.ack.parser.timestamp

import com.inkapplications.ack.parser.SimpleCodec
import com.inkapplications.ack.parser.format.fixedLength
import kotlinx.datetime.*

/**
 * Parse Month/Day/Hours/Minutes format.
 */
internal class MdhmCodec(
    private val clock: Clock = Clock.System,
): SimpleCodec<Instant> {
    override fun encode(data: Instant): String {
        val dateTime = data.toLocalDateTime(TimeZone.UTC)
        val M = dateTime.month.number.fixedLength(2)
        val d = dateTime.dayOfMonth.fixedLength(2)
        val h = dateTime.hour.fixedLength(2)
        val m = dateTime.minute.fixedLength(2)

        return "$M$d$h$m"
    }

    override fun decode(data: String): Instant {
        val month = data.substring(0, 2).toInt()
        val days = data.substring(2, 4).toInt()
        val hours = data.substring(4, 6).toInt()
        val minutes = data.substring(6, 8).toInt()

        return clock.now()
            .withUtcValues(
                month = Month(month),
                dayOfMonth = days,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )
    }
}
