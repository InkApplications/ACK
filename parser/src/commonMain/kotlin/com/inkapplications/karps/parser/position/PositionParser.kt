package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketDataTransformer
import com.inkapplications.karps.parser.altitude.AltitudeChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.requireType
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData

internal class PositionParser(
    private val timestampModule: TimestampModule,
): PacketDataTransformer {
    private val dataTypeChunker = ControlCharacterChunker('!', '/', '@', '=')
    private val timestampParser = timestampModule.timestampChunker

    override fun parse(body: String): PacketData.Position {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val timestamp = timestampParser.parseOptionalAfter(dataTypeIdentifier)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null
        val altitudeComment = AltitudeChunker.parseOptionalAfter(plainExtension ?: position)
        val supportsMessaging = when (dataTypeIdentifier.result) {
            "=", "@" -> true
            else -> false
        }

        return PacketData.Position(
            timestamp = timestamp.result,
            coordinates = position.result.coordinates,
            symbol = position.result.symbol,
            comment = altitudeComment.remainingData,
            altitude = altitudeComment.result
                ?: compressedExtension?.valueFor(CompressedPositionExtensions.AltitudeExtra::class),
            trajectory = compressedExtension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)
                ?: plainExtension?.result?.valueFor(DataExtensions.TrajectoryExtra::class),
            range = compressedExtension?.valueFor(CompressedPositionExtensions.RangeExtra::class)
                ?: plainExtension?.result?.valueFor(DataExtensions.RangeExtra::class),
            transmitterInfo = plainExtension?.result?.valueFor(DataExtensions.TransmitterInfoExtra::class),
            signalInfo = plainExtension?.result?.valueFor(DataExtensions.OmniDfSignalExtra::class),
            directionReportExtra = plainExtension?.result?.valueFor(DataExtensions.DirectionReportExtra::class),
            supportsMessaging = supportsMessaging,
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Position>()

        val identifier = when {
            packet.supportsMessaging && packet.timestamp != null -> '@'
            packet.supportsMessaging -> '='
            packet.timestamp != null -> '/'
            else -> '!'
        }
        val time = packet.timestamp?.let(timestampModule.dhmzCodec::encode).orEmpty()
        val position = PositionCodec.encodeBody(
            config = config,
            coordinates = packet.coordinates,
            symbol = packet.symbol,
            altitude = packet.altitude,
            trajectory = packet.trajectory,
            range = packet.range,
            transmitterInfo = packet.transmitterInfo,
            signalInfo = packet.signalInfo,
            directionReport = packet.directionReportExtra,
        )

        return "$identifier$time$position${packet.comment}"
    }
}
