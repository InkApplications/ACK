package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.common.CharChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.SpanChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.PositionCodec
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketRoute
import com.inkapplications.karps.structures.ReportState

private const val NAME_LENGTH = 9

internal class ObjectTransformer(
    private val timestamps: TimestampModule,
): PacketTransformer {
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

    override fun parse(route: PacketRoute, body: String): AprsPacket.ObjectReport {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val name = nameParser.parseAfter(dataTypeIdentifier)
        val state = stateParser.parseAfter(name)
        val timestamp = timestampParser.parseAfter(state)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null

        return AprsPacket.ObjectReport(
            route = route,
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

    override fun generate(packet: AprsPacket): String = when (packet) {
        is AprsPacket.ObjectReport -> {
            val encodedLocation = PositionCodec.encodeBody(
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
            "$dataTypeCharacter${packet.name.padEnd(NAME_LENGTH, ' ')}${packet.state.symbol}$timestamp$encodedLocation"
        }
        else -> unhandled()
    }
}
