package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.Base91
import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.timestamp.TIMESTAMP
import com.inkapplications.karps.parser.timestamp.TimestampParser
import com.inkapplications.karps.structures.*

class CompressedPositionParser(
    private val timestampParser: TimestampParser
): PacketInformationParser {
    override val supportedDataTypes: CharArray = charArrayOf('!', '/', '@', '=')
    private val format = Regex("""^($TIMESTAMP)?([!-~])([!-|]{4})([!-|]{4})([!-~])(.*)$""")

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val result = format.find(packet.body) ?: throw PacketFormatException("Invalid coordinate format: ${packet.body}")
        val (
            timestamp,
            tableIdentifier,
            latitude,
            longitude,
            codeIdentifier,
            comment
        ) = result.destructured

        return AprsPacket.Position(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            destination = packet.destination,
            source = packet.source,
            digipeaters = packet.digipeaters,
            coordinates = Coordinates(
                latitude = Latitude(90 - (Base91.toInt(latitude) / 380926.0)),
                longitude = Longitude(-180 + (Base91.toInt(longitude) / 190463.0))
            ),
            symbol = symbolOf(
                tableIdentifier = tableIdentifier.single(),
                codeIdentifier = codeIdentifier.single()
            ),
            comment = comment,
            timestamp = runCatching { timestampParser.parse(timestamp) }.getOrNull()
        )
    }
}
