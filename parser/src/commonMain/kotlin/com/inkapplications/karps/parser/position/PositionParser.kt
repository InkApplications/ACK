package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.altitude.AltitudeChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptional
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.timestamp.TimestampChunker
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

class PositionParser(
    clock: Clock = Clock.System,
    timezone: TimeZone = TimeZone.UTC
): PacketTypeParser {
    override val dataTypeFilter = charArrayOf('!', '/', '@', '=')
    private val timestampParser = TimestampChunker(clock, timezone)

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.Position {
        val timestamp = timestampParser.parseOptional(packet)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null
        val altitudeComment = AltitudeChunker.parseOptionalAfter(plainExtension ?: position)

        return AprsPacket.Position(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            raw = packet.raw,
            timestamp = timestamp.result,
            coordinates = position.result.coordinates,
            symbol = position.result.symbol,
            comment = altitudeComment.remainingData,
            altitude = altitudeComment.result ?: compressedExtension?.valueFor(CompressedPositionExtensions.AltitudeExtra::class),
            trajectory = compressedExtension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)
                ?: plainExtension?.result?.valueFor(DataExtensions.TrajectoryExtra::class),
            range = compressedExtension?.valueFor(CompressedPositionExtensions.RangeExtra::class)
                ?: plainExtension?.result?.valueFor(DataExtensions.RangeExtra::class),
            transmitterInfo = plainExtension?.result?.valueFor(DataExtensions.TransmitterInfoExtra::class),
            signalInfo = plainExtension?.result?.valueFor(DataExtensions.OmniDfSignalExtra::class),
            directionReportExtra = plainExtension?.result?.valueFor(DataExtensions.DirectionReportExtra::class)
        )
    }
}
