package com.inkapplications.karps.parser.telemetry

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.SpanChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parse
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.TelemetryValues

class TelemetryParser: PacketTypeParser {
    override val dataTypeFilter: CharArray = charArrayOf('T')
    private val sequenceStartChar = ControlCharacterChunker('#')
    private val sequenceChunk = SpanUntilChunker(
        stopChars = charArrayOf(','),
        maxLength = 3,
        popControlCharacter = true,
    )
    private val analogValueChunk = SpanUntilChunker(
        stopChars = charArrayOf(','),
        required = true,
        popControlCharacter = true,
    ).mapParsed {
        it.toFloat()
    }
    private val digitalValueChunk = SpanChunker(
        length = 8,
    ).mapParsed {
        it.toUByte(2)
    }

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.TelemetryReport {
        val start = sequenceStartChar.parse(packet)
        val sequence = sequenceChunk.parseAfter(start)
        val analog1 = analogValueChunk.parseAfter(sequence)
        val analog2 = analogValueChunk.parseAfter(analog1)
        val analog3 = analogValueChunk.parseAfter(analog2)
        val analog4 = analogValueChunk.parseAfter(analog3)
        val analog5 = analogValueChunk.parseAfter(analog4)
        val digital = digitalValueChunk.parseAfter(analog5)

        val telemetryValues = TelemetryValues(
            analog1 = analog1.result,
            analog2 = analog2.result,
            analog3 = analog3.result,
            analog4 = analog4.result,
            analog5 = analog5.result,
            digital = digital.result,
        )

        return AprsPacket.TelemetryReport(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            sequenceId = sequence.result,
            data = telemetryValues,
            comment = digital.remainingData,
        )
    }
}
