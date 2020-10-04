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
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset

class PositionParser(
    timezone: TimezoneOffset = TimezoneOffset.local(DateTime.now())
): PacketTypeParser {
    override val dataTypeFilter = charArrayOf('!', '/', '@', '=')
    private val timestampParser = TimestampChunker(timezone)

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.Position {
        val timestamp = timestampParser.parseOptional(packet)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedExtension = position.parsed.compressedExtension
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
            timestamp = timestamp.parsed,
            coordinates = position.parsed.coordinates,
            symbol = position.parsed.symbol,
            comment = altitudeComment.remainingData,
            altitude = altitudeComment.parsed ?: compressedExtension?.valueFor(CompressedPositionExtensions.AltitudeExtra::class),
            trajectory = compressedExtension?.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)
                ?: plainExtension?.parsed?.valueFor(DataExtensions.TrajectoryExtra::class),
            range = compressedExtension?.valueFor(CompressedPositionExtensions.RangeExtra::class)
                ?: plainExtension?.parsed?.valueFor(DataExtensions.RangeExtra::class),
            transmitterInfo = plainExtension?.parsed?.valueFor(DataExtensions.TransmitterInfoExtra::class),
            signalInfo = plainExtension?.parsed?.valueFor(DataExtensions.OmniDfSignalExtra::class),
            directionReportExtra = plainExtension?.parsed?.valueFor(DataExtensions.DirectionReportExtra::class)
        )
    }
}
