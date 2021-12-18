package com.inkapplications.karps.parser.status

import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.parseOptional
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.structures.AprsPacket

internal class StatusReportTransformer(
    private val timestampModule: TimestampModule,
): PacketTransformer {
    override val dataTypeFilter: CharArray = charArrayOf('>')

    private val timestampChunker = timestampModule.dhmzChunker

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.StatusReport {
        val time = timestampChunker.parseOptional(packet)
        val status = time.remainingData

        return AprsPacket.StatusReport(
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = time.result,
            status = status,
        )
    }

    override fun generate(packet: AprsPacket): String {
        if (packet !is AprsPacket.StatusReport) unhandled()
        val timestamp = packet.timestamp
            ?.let { timestampModule.dhmzCodec.encode(it) }
            .orEmpty()

        return "$timestamp${packet.status}"
    }
}
