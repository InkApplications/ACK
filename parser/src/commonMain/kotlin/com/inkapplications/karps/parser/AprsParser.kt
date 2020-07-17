package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import com.soywiz.klock.DateTime

class AprsParser(
    private val parsers: List<PacketInformationParser>
) {
    fun fromString(frame: String): AprsPacket {
        val source = frame.substringBefore('>').parseAddress()
        val route = frame.substringAfter('>')
            .substringBefore(':')
            .split(',')
        val destination = route[0].parseAddress()
        val digipeaters = route.slice(1 until route.size).map {
            Digipeater(it.trimEnd('*').parseAddress(), it.endsWith('*'))
        }
        val body = frame.substringAfter(':').let {
            it.slice(1 until it.length)
        }
        val dataType = frame.charAfter(':')
        val prototype = AprsPacket.Unknown(
            received = DateTime.now(),
            dataTypeIdentifier = dataType,
            source = source,
            destination = destination,
            digipeaters = digipeaters,
            body = body
        )

        parsers.filter { prototype.dataTypeIdentifier in it.supportedDataTypes }
            .forEach {
                try {
                    return it.parse(prototype)
                } catch (error: PacketFormatException) {}
            }

        return prototype
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0")
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
