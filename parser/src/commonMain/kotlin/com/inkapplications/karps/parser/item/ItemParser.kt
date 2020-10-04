package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.*
import com.inkapplications.karps.parser.chunk.common.CharChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.extension.DataExtensionChunker
import com.inkapplications.karps.parser.extension.DataExtensions
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.*

class ItemParser: PacketTypeParser {
    override val dataTypeFilter: CharArray? = charArrayOf(')')

    private val nameParser = SpanUntilChunker(
        stopChars = charArrayOf('!', '_'),
        minLength = 3,
        maxLength = 10
    )

    private val stateParser = CharChunker.mapParsed {
        when (it) {
            '!' -> ReportState.Live
            '_' -> ReportState.Kill
            else -> throw PacketFormatException("Unexpected State Identifier: <$it>")
        }
    }

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.ItemReport {
        val name = nameParser.parse(packet)
        val state = stateParser.parseAfter(name)
        val position = MixedPositionChunker.parseAfter(state)
        val compressedExtension = position.parsed.compressedExtension
        val plainExtension = if (compressedExtension == null) {
            DataExtensionChunker.parseOptionalAfter(position)
        } else null

        return AprsPacket.ItemReport(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            name = name.parsed,
            state = state.parsed,
            coordinates = position.parsed.coordinates,
            symbol = position.parsed.symbol,
            comment = plainExtension?.remainingData ?: position.remainingData,
            altitude = compressedExtension?.valueFor(CompressedPositionExtensions.AltitudeExtra::class),
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
