package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
import com.soywiz.klock.DateTime

class AprsParser {
    fun fromString(frame: String): AprsPacket {
        val digipeaters = parseDigipeaters(frame)

        return AprsPacket.Unknown(
            received = DateTime.now(),
            dataTypeIdentifier = '{',
            source = parseSource(frame),
            destination = digipeaters[0],
            digipeaters = digipeaters
        )
    }

    private fun parseSource(frame: String): Address = frame.substringBefore('>').parseAddress()

    private fun parseDigipeaters(frame: String): List<Address> {
        return frame.substringAfter('>')
            .substringBefore(':')
            .split(',')
            .map { it.parseAddress() }
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0").toByte()
    )
}
