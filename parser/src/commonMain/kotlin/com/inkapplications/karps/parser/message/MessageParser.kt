package com.inkapplications.karps.parser.message

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.toAddress

class MessageParser: PacketInformationParser {
    override val dataTypeFilter: CharArray? = charArrayOf(':')

    override fun parse(packet: AprsPacket): AprsPacket {
        if (packet.body[9] != ':') return packet
        val addressee = packet.body.substring(0, 8)

        return AprsPacket.Message(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = packet.timestamp,
            addressee = addressee.trim().toAddress(),
            message = packet.body.substring(10).substringBefore('{'),
            body = ""
        )
    }
}
