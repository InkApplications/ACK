package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketDataParser
import com.inkapplications.karps.parser.altitude.AltitudeChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.PacketData

internal class PositionParser(
    timestampModule: TimestampModule,
): PacketDataParser {
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
}
