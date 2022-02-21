package com.inkapplications.ack.parser.item

import com.inkapplications.ack.parser.PacketFormatException
import com.inkapplications.ack.parser.PacketDataTransformer
import com.inkapplications.ack.parser.chunk.common.CharChunker
import com.inkapplications.ack.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.parser.chunk.common.SpanChunker
import com.inkapplications.ack.parser.chunk.mapParsed
import com.inkapplications.ack.parser.chunk.parseAfter
import com.inkapplications.ack.parser.chunk.parseOptionalAfter
import com.inkapplications.ack.parser.extension.DataExtensionChunker
import com.inkapplications.ack.parser.extension.DataExtensions
import com.inkapplications.ack.parser.position.CompressedPositionExtensions
import com.inkapplications.ack.parser.position.MixedPositionChunker
import com.inkapplications.ack.parser.position.PositionCodec
import com.inkapplications.ack.parser.position.compressedExtension
import com.inkapplications.ack.parser.requireType
import com.inkapplications.ack.parser.timestamp.TimestampModule
import com.inkapplications.ack.parser.valueFor
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.ReportState

private const val NAME_LENGTH = 9

internal class ObjectTransformer(
    private val timestamps: TimestampModule,
): PacketDataTransformer {
    private val dataTypeCharacter = ';'
    private val dataTypeChunker = ControlCharacterChunker(dataTypeCharacter)

    private val nameParser = SpanChunker(NAME_LENGTH).mapParsed {
        it.trim()
    }

    private val stateParser = CharChunker.mapParsed { char ->
        ReportState.values().firstOrNull { it.symbol == char } ?: throw PacketFormatException("Unexpected State Identifier: <$char>")
    }

    private val ReportState.symbol get() = when (this) {
        ReportState.Live -> '*'
        ReportState.Kill -> '_'
    }

    private val timestampParser = timestamps.timestampChunker

    override fun parse(body: String): PacketData.ObjectReport {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val name = nameParser.parseAfter(dataTypeIdentifier)
        val state = stateParser.parseAfter(name)
        val timestamp = timestampParser.parseAfter(state)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null

        return PacketData.ObjectReport(
            timestamp = timestamp.result,
            name = name.result,
            state = state.result,
            coordinates = position.result.coordinates,
            symbol = position.result.symbol,
            comment = plainExtension?.remainingData ?: position.remainingData,
            altitude = compressedExtension?.valueFor(CompressedPositionExtensions.AltitudeExtra::class),
            trajectory = compressedExtension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)
                ?: plainExtension?.result?.valueFor(DataExtensions.TrajectoryExtra::class),
            range = compressedExtension?.valueFor(CompressedPositionExtensions.RangeExtra::class)
                ?: plainExtension?.result?.valueFor(DataExtensions.RangeExtra::class),
            transmitterInfo = plainExtension?.result?.valueFor(DataExtensions.TransmitterInfoExtra::class),
            signalInfo = plainExtension?.result?.valueFor(DataExtensions.OmniDfSignalExtra::class),
            directionReport = plainExtension?.result?.valueFor(DataExtensions.DirectionReportExtra::class)
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.ObjectReport>()
        val encodedLocation = PositionCodec.encodeBody(
            config = config,
            coordinates = packet.coordinates,
            symbol = packet.symbol,
            altitude = packet.altitude,
            trajectory = packet.trajectory,
            range = packet.range,
            transmitterInfo = packet.transmitterInfo,
            signalInfo = packet.signalInfo,
            directionReport = packet.directionReport
        )
        val timestamp = packet.timestamp?.let { timestamps.dhmzCodec.encode(it) }.orEmpty()

        return "$dataTypeCharacter${packet.name.padEnd(NAME_LENGTH, ' ')}${packet.state.symbol}$timestamp$encodedLocation"
    }
}
