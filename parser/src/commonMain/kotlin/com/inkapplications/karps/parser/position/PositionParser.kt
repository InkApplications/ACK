package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.timestamp.TIMESTAMP
import com.inkapplications.karps.parser.timestamp.TimestampParser
import com.inkapplications.karps.structures.AprsPacket

/**
 * Parse a position packet.
 */
class PositionParser(
    private val timestampParser: TimestampParser
): PacketInformationParser {
    override val supportedDataTypes = charArrayOf('!', '/', '@', '=')
    private val format = Regex("""^($TIMESTAMP)?(${PositionDataParser.format.pattern})(.*)$""")

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val result = format.find(packet.body) ?: throw PacketFormatException("Invalid coordinate format: ${packet.body}")

        val timestamp = runCatching { timestampParser.parse(result.groupValues[1]) }.getOrNull()
        val data = PositionDataParser.parse(result.groupValues[2])
        val comment = result.groupValues[result.groupValues.size - 1]

        return AprsPacket.Position(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            coordinates = data.coordinates,
            symbol = data.symbol,
            comment = comment,
            altitude = (data.extra as? PositionExtraUnion.Altitude)?.value,
            trajectory = (data.extra as? PositionExtraUnion.Course)?.value,
            range = (data.extra as? PositionExtraUnion.Range)?.value,
            timestamp = timestamp
        )
    }
}
