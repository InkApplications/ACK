package com.inkapplications.karps.parser.status

import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.structures.PacketData

internal class StatusReportTransformer(
    private val timestampModule: TimestampModule,
): PacketTransformer {
    private val dataTypeCharacter = '>'
    private val dataTypeChunker = ControlCharacterChunker(dataTypeCharacter)

    private val timestampChunker = timestampModule.dhmzChunker

    override fun parse(body: String): PacketData.StatusReport {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val time = timestampChunker.parseOptionalAfter(dataTypeIdentifier)
        val status = time.remainingData

        return PacketData.StatusReport(
            timestamp = time.result,
            status = status,
        )
    }

    override fun generate(packet: PacketData): String {
        if (packet !is PacketData.StatusReport) unhandled()
        val timestamp = packet.timestamp
            ?.let { timestampModule.dhmzCodec.encode(it) }
            .orEmpty()

        return "$dataTypeCharacter$timestamp${packet.status}"
    }
}
