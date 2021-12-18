package com.inkapplications.karps.parser.capabilities

import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.common.CsvChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parse
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.capabilityOf

class CapabilitiesTransformer: PacketTransformer {
    override val dataTypeFilter = charArrayOf('<')

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
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            capabilityData = capabilities.result,
        )
    }

    override fun generate(packet: AprsPacket): String = when (packet) {
        is AprsPacket.CapabilityReport -> packet.capabilityData.joinToString(",") { it.toString() }
        else -> unhandled()
    }
}
