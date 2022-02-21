package com.inkapplications.ack.codec.status

import com.inkapplications.ack.codec.PacketDataTransformer
import com.inkapplications.ack.codec.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.codec.chunk.parseOptionalAfter
import com.inkapplications.ack.codec.requireType
import com.inkapplications.ack.codec.timestamp.TimestampModule
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData

internal class StatusReportTransformer(
    private val timestampModule: TimestampModule,
): PacketDataTransformer {
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

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.StatusReport>()
        val timestamp = packet.timestamp
            ?.let { timestampModule.dhmzCodec.encode(it) }
            .orEmpty()

        return "$dataTypeCharacter$timestamp${packet.status}"
    }
}
