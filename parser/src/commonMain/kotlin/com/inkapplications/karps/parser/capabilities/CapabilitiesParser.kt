package com.inkapplications.karps.parser.capabilities

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.common.CsvChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parse
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.capabilityOf

class CapabilitiesParser: PacketTypeParser {
    override val dataTypeFilter: CharArray = charArrayOf('<')

    private val capabilitiesChunker = CsvChunker(',')
        .mapParsed {
            it.map {
                it.split('=').let {
                    capabilityOf(it.get(0).trim(), it.getOrNull(1)?.trim())
                }
            }.toSet()
        }

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.CapabilityReport {
        val capabilities = capabilitiesChunker.parse(packet)

        return AprsPacket.CapabilityReport(
            raw = packet.raw,
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            capabilityData = capabilities.result,
        )
    }
}
