package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.substringIsNumeric
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month

/**
 * Parse Month/Day/Hours/Minutes format.
 */
class MdhmParser: PacketInformationParser {
    override fun parse(packet: AprsPacket): AprsPacket {
        if (packet.body.length < 8 || !packet.body.substringIsNumeric(0, 8)) {
            return packet
        }

        val month = packet.body.substring(0, 2).toInt().takeIf { it != 0 } ?: return packet
        val days = packet.body.substring(2, 4).toInt().takeIf { it != 0 } ?: return packet
        val hours = packet.body.substring(4, 6).toInt()
        val minutes = packet.body.substring(6, 8).toInt()

        val timestamp = DateTime.now()
            .copyDayOfMonth(
                month = Month.get(month),
                dayOfMonth = days,
                hours = hours,
                minutes = minutes,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        val body = packet.body.substring(8)

        return packet.withTimestamp(timestamp).withBody(body)
    }
}
