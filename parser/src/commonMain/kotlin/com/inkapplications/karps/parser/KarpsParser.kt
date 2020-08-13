package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger

internal class KarpsParser(
    private val infoParsers: Array<PacketInformationParser>,
    private val logger: KimchiLogger = EmptyLogger,
    private val clock: Clock = SystemClock
): AprsParser {
    override fun fromString(packet: String): AprsPacket {
        logger.trace("Parsing packet: $packet")
        val source = packet.substringBefore('>').parseAddress()
        val route = packet.substringAfter('>')
            .substringBefore(':')
            .split(',')
        val destination = route[0].parseAddress()
        val digipeaters = route.slice(1 until route.size).map {
            Digipeater(it.trimEnd('*').parseAddress(), it.endsWith('*'))
        }
        val body = packet.substringAfter(':').let {
            it.substring(1)
        }
        val dataType = packet.charAfter(':')

        val prototype: AprsPacket = AprsPacket.Unknown(
            received = clock.current,
            dataTypeIdentifier = dataType,
            source = source,
            destination = destination,
            digipeaters = digipeaters,
            body = body
        )

        return infoParsers.fold(prototype) { newPrototype, parser ->
            if (parser.dataTypeFilter == null || newPrototype.dataTypeIdentifier in parser.dataTypeFilter!!)
                parser.parse(newPrototype)
            else
                newPrototype
        }
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0")
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
