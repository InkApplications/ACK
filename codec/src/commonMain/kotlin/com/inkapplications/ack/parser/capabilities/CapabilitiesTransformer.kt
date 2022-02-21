package com.inkapplications.ack.parser.capabilities

import com.inkapplications.ack.parser.PacketDataTransformer
import com.inkapplications.ack.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.parser.chunk.common.CsvChunker
import com.inkapplications.ack.parser.chunk.mapParsed
import com.inkapplications.ack.parser.chunk.parseAfter
import com.inkapplications.ack.parser.requireType
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilityOf

class CapabilitiesTransformer: PacketDataTransformer {
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

    override fun parse(body: String): PacketData.CapabilityReport {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val capabilities = capabilitiesChunker.parseAfter(dataTypeIdentifier)

        return PacketData.CapabilityReport(
            capabilityData = capabilities.result,
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.CapabilityReport>()

        val capabilities = packet.capabilityData.joinToString(",") { it.toString() }

        return "$dataTypeCharacter$capabilities"
    }
}
