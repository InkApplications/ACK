package com.inkapplications.karps.parser.altitude

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.feet

/**
 * Parse Altitude from a packet's comment field.
 *
 * Because the comment can appear *anywhere* in the comment field, this
 * will parse and remove anything resembling an altitude and should therefore
 * be run as late in the parsing process as possible.
 * This also expects the packet type to have been parsed already, as not
 * all packets support altitudes.
 * If the packet type does not support an altitude, this parser will not
 * parse or remove the altitude text from the packet's body.
 */
class AltitudeCommentParser: PacketInformationParser {
    private val format = Regex("""\/A=(\d{6})""")

    override fun parse(packet: AprsPacket): AprsPacket {
        val result = format.find(packet.body) ?: return packet
        val altitude = result.groupValues[1].toInt().feet
        val newBody = packet.body.replace(format, "")

        return when(packet) {
            is AprsPacket.Position -> packet.copy(
                altitude = altitude,
                body = newBody
            )
            is AprsPacket.Weather -> packet.copy(
                altitude = altitude,
                body = newBody
            )
            is AprsPacket.ObjectReport,
            is AprsPacket.Unknown -> packet
        }
    }
}
