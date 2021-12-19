package com.inkapplications.karps.parser.telemetry

import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.SpanChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.format.fixedLength
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.structures.PacketData
import com.inkapplications.karps.structures.TelemetryValues
import kotlin.math.roundToInt

class TelemetryTransformer: PacketTransformer {
    private val dataTypeCharacter = 'T'
    private val dataTypeChunker = ControlCharacterChunker(dataTypeCharacter)
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

    override fun parse(body: String): PacketData.TelemetryReport {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val start = sequenceStartChar.parseAfter(dataTypeIdentifier)
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

        return PacketData.TelemetryReport(
            sequenceId = sequence.result,
            data = telemetryValues,
            comment = digital.remainingData,
        )
    }

    override fun generate(packet: PacketData): String {
        if (packet !is PacketData.TelemetryReport) unhandled()
        val sequence = packet.sequenceId.fixedLength(3)
        val useFloat = packet.data.analogValues.all { it < 10 }
        val analogStrings = packet.data.analogValues.joinToString(",") {
            if (useFloat) {
                it.fixedLength(decimals = 1, leftDigits = 1)
            } else {
                it.roundToInt().fixedLength(3)
            }
        }
        val digitalString = packet.data.digital.toString(2).toInt().fixedLength(8)
        return "$dataTypeCharacter#$sequence,$analogStrings,$digitalString${packet.comment}"
    }
}
