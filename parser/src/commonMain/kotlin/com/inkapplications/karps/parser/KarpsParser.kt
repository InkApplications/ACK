package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import com.soywiz.klock.DateTime

internal class KarpsParser(
    private val infoParser: PacketInformationParser
): AprsParser {
    override fun fromString(packet: String): AprsPacket {
        val source = packet.substringBefore('>').parseAddress()
        val route = packet.substringAfter('>')
            .substringBefore(':')
            .split(',')
        val destination = route[0].parseAddress()
        val digipeaters = route.slice(1 until route.size).map {
            Digipeater(it.trimEnd('*').parseAddress(), it.endsWith('*'))
        }
        val body = packet.substringAfter(':').let {
            it.slice(1 until it.length)
        }
        val dataType = packet.charAfter(':')
        val prototype = AprsPacket.Unknown(
            received = DateTime.now(),
            dataTypeIdentifier = dataType,
            source = source,
            destination = destination,
            digipeaters = digipeaters,
            body = body
        )

        return try {
            infoParser.parse(prototype)
        } catch (error: PacketFormatException) {
            prototype
        }
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0")
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
