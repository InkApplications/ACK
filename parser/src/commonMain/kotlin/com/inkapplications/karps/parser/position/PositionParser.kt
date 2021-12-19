package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.altitude.AltitudeChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketRoute

internal class PositionParser(
    timestampModule: TimestampModule,
): PacketTypeParser {
    private val dataTypeChunker = ControlCharacterChunker('!', '/', '@', '=')
    private val timestampParser = timestampModule.timestampChunker

    override fun parse(route: PacketRoute, body: String): AprsPacket.Position {
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

        return AprsPacket.Position(
            route = route,
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
}
