package com.inkapplications.ack.codec.timestamp

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.chunk.requireControl
import com.inkapplications.ack.codec.format.fixedLength
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Parse Days/Hours/Minutes for UTC times.
 */
internal class DhmzCodec(
    private val clock: Clock = Clock.System,
): SimpleCodec<Instant> {
    private val control = 'z'
    override fun encode(data: Instant): String {
        val dateTime = data.toLocalDateTime(TimeZone.UTC)
        val d = dateTime.dayOfMonth.fixedLength(2)
        val h = dateTime.hour.fixedLength(2)
        val m = dateTime.minute.fixedLength(2)

        return "$d$h$m$control"
    }

    override fun decode(data: String): Instant {
        data[6].requireControl(control)
        val days = data.substring(0, 2).toInt()
        val hours = data.substring(2, 4).toInt()
        val minutes = data.substring(4, 6).toInt()

        return clock.now().withUtcValues(
            dayOfMonth = days,
            hour = hours,
            minute = minutes,
            second = 0,
            nanosecond = 0
        )
    }
}
