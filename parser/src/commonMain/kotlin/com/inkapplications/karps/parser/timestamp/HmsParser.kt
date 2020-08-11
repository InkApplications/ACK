package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.substringIsNumeric
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime

/**
 * Parse Hours/Minutes/Seconds.
 */
class HmsParser: PacketInformationParser {
    override fun parse(data: PacketInformation): PacketInformation {
        if (data.body.length < 7 || data.body[6] != 'h' || !data.body.substringIsNumeric(0, 6)) {
            return data
        }

        val hours = data.body.substring(0, 2).toInt().takeIf { it != 0 } ?: return data
        val minutes = data.body.substring(2, 4).toInt()
        val seconds = data.body.substring(4, 6).toInt()

        val timestamp = DateTime.now()
            .copyDayOfMonth(
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        return data.copy(
            body = data.body.substring(7),
            timestamp = timestamp
        )
    }
}
