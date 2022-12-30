package com.inkapplications.ack.codec.item

import com.inkapplications.ack.codec.PacketDataTransformer
import com.inkapplications.ack.codec.PacketFormatException
import com.inkapplications.ack.codec.chunk.common.CharChunker
import com.inkapplications.ack.codec.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.codec.chunk.common.SpanUntilChunker
import com.inkapplications.ack.codec.chunk.mapParsed
import com.inkapplications.ack.codec.chunk.parseAfter
import com.inkapplications.ack.codec.chunk.parseOptionalAfter
import com.inkapplications.ack.codec.extension.DataExtensionChunker
import com.inkapplications.ack.codec.extension.DataExtensions
import com.inkapplications.ack.codec.position.CompressedPositionExtensions
import com.inkapplications.ack.codec.position.MixedPositionChunker
import com.inkapplications.ack.codec.position.PositionCodec
import com.inkapplications.ack.codec.position.compressedExtension
import com.inkapplications.ack.codec.requireType
import com.inkapplications.ack.codec.valueFor
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.ReportState

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
