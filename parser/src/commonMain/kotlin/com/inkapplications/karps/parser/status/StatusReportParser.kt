package com.inkapplications.karps.parser.status

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.parseOptional
import com.inkapplications.karps.parser.timestamp.DhmzChunker
import com.inkapplications.karps.structures.AprsPacket
import kotlinx.datetime.Clock

class StatusReportParser(
    private val clock: Clock = Clock.System,
): PacketTypeParser {
    override val dataTypeFilter: CharArray = charArrayOf('>')

    private val timestampChunker = DhmzChunker(clock)

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.StatusReport {
        val time = timestampChunker.parseOptional(packet)
        val status = time.remainingData

        return AprsPacket.StatusReport(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            time = time.result,
            status = status,
        )
    }
}
