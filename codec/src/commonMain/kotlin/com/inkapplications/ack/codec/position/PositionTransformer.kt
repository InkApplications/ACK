package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.PacketDataTransformer
import com.inkapplications.ack.codec.altitude.AltitudeChunker
import com.inkapplications.ack.codec.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.codec.chunk.parseAfter
import com.inkapplications.ack.codec.chunk.parseOptionalAfter
import com.inkapplications.ack.codec.extension.DataExtensionChunker
import com.inkapplications.ack.codec.extension.DataExtensions
import com.inkapplications.ack.codec.requireType
import com.inkapplications.ack.codec.timestamp.TimestampModule
import com.inkapplications.ack.codec.valueFor
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData

internal class PositionTransformer(
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
