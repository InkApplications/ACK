package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketTransformer
import com.inkapplications.karps.parser.chunk.common.CharChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parse
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.PositionCodec
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.unhandled
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState

internal class ItemTransformer: PacketTransformer {
    override val dataTypeFilter = charArrayOf(')')

    private val nameParser = SpanUntilChunker(
        stopChars = charArrayOf('!', '_'),
        minLength = 3,
        maxLength = 10
    )

    private val stateParser = CharChunker.mapParsed { char ->
        ReportState.values().firstOrNull { it.symbol == char } ?: throw PacketFormatException("Unexpected State Identifier: <$char>")
    }

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.ItemReport {
        val name = nameParser.parse(packet)
        val state = stateParser.parseAfter(name)
        val position = MixedPositionChunker.parseAfter(state)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null

        return AprsPacket.ItemReport(
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
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
        is AprsPacket.ItemReport -> {
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
            "${packet.name}${packet.state.symbol}${encodedLocation}${packet.comment}"
        }
        else -> unhandled()
    }

    private val ReportState.symbol get() = when (this) {
        ReportState.Live -> '!'
        ReportState.Kill -> '_'
    }
}
