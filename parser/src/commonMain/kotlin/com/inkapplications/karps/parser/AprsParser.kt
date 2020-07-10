package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
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

        val body = frame.substringAfter(':')

        return AprsPacket.Unknown(
            received = DateTime.now(),
            dataTypeIdentifier = body[0],
            source = source,
            destination = destination,
            digipeaters = digipeaters
        )
    }
    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0").toByte()
    )
}
