package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*

object PlainPositionParser: PacketInformationParser {
    override val supportedDataTypes = arrayOf('!')
    private val format = Regex("""^([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NsSs]).([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw]).""")

    private fun fromStringBody(body: String): Coordinates {
        val result = format.find(body) ?: throw PacketFormatException("Invalid coordinate format: $body")

        val (
            latDegrees,
            latMinutes,
            latSeconds,
            latCardinal,
            longDegrees,
            longMinutes,
            longSeconds,
            longCardinal
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

        return Coordinates(latitude, longitude)
    }

    private val String.value: Double get() = replace(' ', '0').takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        if (packet.dataTypeIdentifier !in arrayOf('!')) throw PacketFormatException("${packet.dataTypeIdentifier} is not a position datatype")
        return AprsPacket.Position(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            coordinates = fromStringBody(packet.body)
        )
    }
}
