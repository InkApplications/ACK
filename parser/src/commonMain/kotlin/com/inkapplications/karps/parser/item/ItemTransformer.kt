package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketDataTransformer
import com.inkapplications.karps.parser.chunk.common.CharChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.PositionCodec
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.requireType
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData
import com.inkapplications.karps.structures.ReportState

internal class ItemTransformer: PacketDataTransformer {
    private val dataTypeCharacter = ')'
    private val dataTypeCunker = ControlCharacterChunker(dataTypeCharacter)
    private val nameParser = SpanUntilChunker(
        stopChars = charArrayOf('!', '_'),
        minLength = 3,
        maxLength = 10
    )

    private val stateParser = CharChunker.mapParsed { char ->
        ReportState.values().firstOrNull { it.symbol == char } ?: throw PacketFormatException("Unexpected State Identifier: <$char>")
    }

    override fun parse(body: String): PacketData.ItemReport {
        val dataTypeIdentifier = dataTypeCunker.popChunk(body)
        val name = nameParser.parseAfter(dataTypeIdentifier)
        val state = stateParser.parseAfter(name)
        val position = MixedPositionChunker.parseAfter(state)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null

        return PacketData.ItemReport(
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
        packet.requireType<PacketData.ItemReport>()

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

        return "$dataTypeCharacter${packet.name}${packet.state.symbol}${encodedLocation}${packet.comment}"
    }

    private val ReportState.symbol get() = when (this) {
        ReportState.Live -> '!'
        ReportState.Kill -> '_'
    }
}
