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
    private val format = Regex("""^($TIMESTAMP)?([0-9\s]{2})([0-9\s]{2})[!-~]([0-9\s]{2})([NnSs]).([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw])[!-~](.*)$""")
    private val String.value: Double get() = replace(' ', '0').takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val result = format.find(packet.body) ?: throw PacketFormatException("Invalid coordinate format: ${packet.body}")

        val (
            timestamp,
            latDegrees,
            latMinutes,
            latSeconds,
            latCardinal,
            longDegrees,
            longMinutes,
            longSeconds,
            longCardinal,
            comment
        ) = result.destructured

        val latitude = Latitude(
            degrees = latDegrees.value.toInt(),
            minutes = latMinutes.value.toInt(),
            seconds = latSeconds.value * 0.6,
            cardinal = latCardinal.single().toCardinal()
        )
        val longitude = Longitude(
            degrees = longDegrees.value.toInt(),
            minutes = longMinutes.value.toInt(),
            seconds = longSeconds.value * 0.6,
            cardinal = longCardinal.single().toCardinal()
        )

        return AprsPacket.Position(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            coordinates = Coordinates(latitude, longitude),
            comment = comment,
            timestamp = runCatching { timestampParser.parse(timestamp) }.getOrNull()
        )
    }
}
