package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.timestamp.TIMESTAMP
import com.inkapplications.karps.parser.timestamp.TimestampParser
import com.inkapplications.karps.structures.*

class PlainPositionParser(
    private val timestampParser: TimestampParser
): PacketInformationParser {
    override val supportedDataTypes = arrayOf('!', '/', '@', '=')
    private val format = Regex("""^($TIMESTAMP)?([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NnSs])([!-~])([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw])([!-~])(.*)$""")
    private val String.value: Double get() = replace(' ', '0').takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val result = format.find(packet.body) ?: throw PacketFormatException("Invalid coordinate format: ${packet.body}")

        val timestamp = runCatching { timestampParser.parse(result.groupValues[1]) }.getOrNull()
        val latDegrees = result.groupValues[2].value.toInt()
        val latMinutes = result.groupValues[3].value.toInt()
        val latSeconds = result.groupValues[4].value * .6
        val latCardinal = result.groupValues[5].single().toCardinal()
        val tableIdentifier = result.groupValues[6].single()
        val longDegrees = result.groupValues[7].value.toInt()
        val longMinutes = result.groupValues[8].value.toInt()
        val longSeconds = result.groupValues[9].value * .6
        val longCardinal = result.groupValues[10].single().toCardinal()
        val codeIdentifier = result.groupValues[11].single()
        val comment = result.groupValues[12]

        val latitude = Latitude(
            degrees = latDegrees,
            minutes = latMinutes,
            seconds = latSeconds,
            cardinal = latCardinal
        )
        val longitude = Longitude(
            degrees = longDegrees,
            minutes = longMinutes,
            seconds = longSeconds,
            cardinal = longCardinal
        )

        return AprsPacket.Position(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            coordinates = Coordinates(latitude, longitude),
            symbol = symbolOf(
                tableIdentifier = tableIdentifier,
                codeIdentifier = codeIdentifier
            ),
            comment = comment,
            timestamp = timestamp
        )
    }
}
