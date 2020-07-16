package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Coordinates
import com.inkapplications.karps.structures.Latitude
import com.inkapplications.karps.structures.Longitude

object CompressedPositionParser: PacketInformationParser {
    override val supportedDataTypes: Array<Char> = arrayOf('!', '/', '@', '=')
    private val format = Regex("""^(${TIMESTAMP})?[!-~]([!-|]{4})([!-|]{4})[!-~](.*)$""")

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val result = format.find(packet.body) ?: throw PacketFormatException("Invalid coordinate format: ${packet.body}")
        val (timestamp, latitude, longitude, comment) = result.destructured

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
            comment = comment,
            timestamp = runCatching { timestamp.takeUnless { it.isEmpty() }?.let { Timestamps().parse(it) } }.getOrNull()
        )
    }
}
