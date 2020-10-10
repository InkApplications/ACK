package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.datetime.Clock

internal class KarpsParser(
    private val infoParsers: Array<PacketTypeParser>,
    private val logger: KimchiLogger = EmptyLogger,
    private val clock: Clock = Clock.System
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

        val prototype = AprsPacket.Unknown(
            received = clock.now(),
            dataTypeIdentifier = dataType,
            source = source,
            destination = destination,
            digipeaters = digipeaters,
            body = body
        )

        infoParsers
            .filter { parser ->
                parser.dataTypeFilter?.let { dataType in it } ?: true
            }
            .forEach { parser ->
                try {
                    return parser.parse(prototype)
                } catch (error: Throwable) {
                    logger.debug(error) { "${parser::class.simpleName} failed to parse: ${error.message}" }
                }
            }
        logger.warn("No parser was able to parse packet.")
        return prototype
    }

    override fun fromAx25(packet: ByteArray): AprsPacket {
        logger.trace("Parsing packet from bytes: $packet")
        val unsignedByteArray = packet.toUByteArray()

        val destination = Address(
            callsign = unsignedByteArray.slice(0..5).toCallsign(),
            ssid = unsignedByteArray[6].toSsid()
        )
        val source = Address(
            callsign = unsignedByteArray.slice(7..12).toCallsign(),
            ssid = unsignedByteArray[13].toSsid()
        )
        val lastDigipeater = unsignedByteArray.drop(14).indexOfFirst { it.toInt() and 1 == 1 }
        val digipeaters = unsignedByteArray
            .drop(14)
            .take(lastDigipeater + 1)
            .chunked(7)
            .map { Address(it.slice(0..5).toCallsign(), it[6].toSsid()) }
            .map { Digipeater(it) }

        val dataType = packet[17 + lastDigipeater].toChar()
        val body = packet.drop(18 + lastDigipeater).map { it.toChar() }.toCharArray().let { String(it) }

        val prototype = AprsPacket.Unknown(
            received = clock.now(),
            dataTypeIdentifier = dataType,
            source = source,
            destination = destination,
            digipeaters = digipeaters,
            body = body
        )

        infoParsers
            .filter { parser ->
                parser.dataTypeFilter?.let { dataType in it } ?: true
            }
            .forEach { parser ->
                try {
                    return parser.parse(prototype)
                } catch (error: Throwable) {
                    logger.debug(error) { "${parser::class.simpleName} failed to parse: ${error.message}" }
                }
            }
        logger.warn("No parser was able to parse packet.")
        return prototype
    }

    private fun List<UByte>.toCallsign(): String {
        return map { it.toInt() shr 1 }
            .map { it.toChar() }
            .toCharArray()
            .let { String(it) }
            .trim()
    }

    private fun UByte.toSsid(): String {
        return let { it and 0b01111110u }
            .let { it.toInt() shr 1 }
            .let { it.toChar() }
            .toString()
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0")
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
