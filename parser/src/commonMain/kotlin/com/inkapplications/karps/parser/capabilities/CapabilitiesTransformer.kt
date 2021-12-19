package com.inkapplications.karps.parser.capabilities

import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.CsvChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketRoute
import com.inkapplications.karps.structures.capabilityOf

class CapabilitiesTransformer: PacketTransformer {
    private val dataTypeCharacter = '<'
    private val dataTypeChunker = ControlCharacterChunker(dataTypeCharacter)

    private val capabilitiesChunker = CsvChunker(',')
        .mapParsed {
            it.map {
                it.split('=').let {
                    capabilityOf(it.get(0).trim(), it.getOrNull(1)?.trim())
                }
            }.toSet()
        }

    override fun parse(route: PacketRoute, body: String): AprsPacket.CapabilityReport {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val capabilities = capabilitiesChunker.parseAfter(dataTypeIdentifier)

        return AprsPacket.CapabilityReport(
            route = route,
            capabilityData = capabilities.result,
        )
    }

    override fun generate(packet: AprsPacket): String {
        if (packet !is AprsPacket.CapabilityReport) unhandled()

        val capabilities = packet.capabilityData.joinToString(",") { it.toString() }

        return "$dataTypeCharacter$capabilities"
    }
}
