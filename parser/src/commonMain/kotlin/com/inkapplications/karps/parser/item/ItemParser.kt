package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState

class ItemParser: PacketInformationParser {
    override val dataTypeFilter: CharArray? = charArrayOf(')')
    private val format = Regex("""^([^!_]{3,9})([!_])""")

    override fun parse(packet: AprsPacket): AprsPacket {
        val result = format.find(packet.body) ?: return packet

        val state = when (result.groupValues[2].single()) {
            '!' -> ReportState.Live
            '_' -> ReportState.Kill
            else -> throw IllegalStateException("Missing state identifier.")
        }

        return AprsPacket.ItemReport(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = packet.timestamp,
            state = state,
            name = result.groupValues[1].trim(),
            body = packet.body.substring(result.groupValues[0].length)
        )
    }
}
