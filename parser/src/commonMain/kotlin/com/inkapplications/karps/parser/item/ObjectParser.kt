package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState

class ObjectParser: PacketInformationParser {
    override val dataTypeFilter: CharArray? = charArrayOf(';')

    override fun parse(packet: AprsPacket): AprsPacket {
        val state = when (packet.body[9]) {
            '*' -> ReportState.Live
            '_' -> ReportState.Kill
            else -> return packet
        }

        return AprsPacket.ObjectReport(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = packet.timestamp,
            state = state,
            name = packet.body.substring(0, 8).trim(),
            body = packet.body.substring(10)
        )
    }
}
