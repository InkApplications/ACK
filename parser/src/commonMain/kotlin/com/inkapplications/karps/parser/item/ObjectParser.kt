package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.*
import com.inkapplications.karps.parser.chunk.common.CharChunker
import com.inkapplications.karps.parser.chunk.common.SpanChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parse
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.timestamp.TimestampChunker
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

class ObjectParser(
    clock: Clock = Clock.System,
    timezone: TimeZone = TimeZone.UTC
): PacketTypeParser {
    override val dataTypeFilter: CharArray? = charArrayOf(';')

    private val nameParser = SpanChunker(9).mapParsed {
        it.trim()
    }

    private val stateParser = CharChunker.mapParsed {
        when (it) {
            '*' -> ReportState.Live
            '_' -> ReportState.Kill
            else -> throw PacketFormatException("Unexpected State Identifier: <$it>")
        }
    }

    private val timestampParser = TimestampChunker(clock, timezone)

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.ObjectReport {
        val name = nameParser.parse(packet)
        val state = stateParser.parseAfter(name)
        val timestamp = timestampParser.parseAfter(state)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedExtension = position.result.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null

        return AprsPacket.ObjectReport(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            raw = packet.raw,
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
            directionReportExtra = plainExtension?.result?.valueFor(DataExtensions.DirectionReportExtra::class)
        )
    }
}
