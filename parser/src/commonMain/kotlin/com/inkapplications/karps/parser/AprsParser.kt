package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import com.soywiz.klock.DateTime

class AprsParser {
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

        return when (dataType) {
            '!', '=' -> AprsPacket.Position(
                received = DateTime.now(),
                dataTypeIdentifier = dataType,
                source = source,
                destination = destination,
                digipeaters = digipeaters,
                coordinates = CoordinatesParser.fromStringBody(body)
            )
            else -> AprsPacket.Unknown(
                received = DateTime.now(),
                dataTypeIdentifier = dataType,
                source = source,
                destination = destination,
                digipeaters = digipeaters
            )
        }
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0").toByte()
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
