package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.substringIsNumeric
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime

/**
 * Parse Days/Hours/Minutes for UTC times.
 */
class DhmzParser: PacketInformationParser {
    override fun parse(packet: AprsPacket): AprsPacket {
        if (packet.body.length < 7 || packet.body[6] != 'z' || !packet.body.substringIsNumeric(0, 6)) {
            return packet
        }

        val days = packet.body.substring(0, 2).toInt().takeIf { it != 0 } ?: return packet
        val hours = packet.body.substring(2, 4).toInt()
        val minutes = packet.body.substring(4, 6).toInt()
        val timestamp = DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = days,
                hours = hours,
                minutes = minutes,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        val body = packet.body.substring(7)

        return packet.withTimestamp(timestamp).withBody(body)
    }
}
