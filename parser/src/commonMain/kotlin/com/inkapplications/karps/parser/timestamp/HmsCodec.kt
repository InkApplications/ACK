package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.SimpleCodec
import com.inkapplications.karps.parser.chunk.requireControl
import com.inkapplications.karps.parser.format.fixedLength
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Parse Hours/Minutes/Seconds.
 */
internal class HmsCodec(
    private val clock: Clock = Clock.System,
): SimpleCodec<Instant> {
    private val control = 'h'

    override fun encode(data: Instant): String {
        val dateTime = data.toLocalDateTime(TimeZone.UTC)
        val h = dateTime.hour.fixedLength(2)
        val m = dateTime.minute.fixedLength(2)
        val s = dateTime.second.fixedLength(2)

        return "$h$m$s$control"
    }

    override fun decode(data: String): Instant {
        data[6].requireControl(control)

        val hours = data.substring(0, 2).toInt()
        val minutes = data.substring(2, 4).toInt()
        val seconds = data.substring(4, 6).toInt()

        return clock.now()
            .withUtcValues(
                hour = hours,
                minute = minutes,
                second = seconds,
                nanosecond = 0
            )
    }
}
