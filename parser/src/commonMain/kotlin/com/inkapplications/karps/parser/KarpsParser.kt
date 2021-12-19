package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger

internal class KarpsParser(
    private val infoParsers: Array<PacketTypeParser>,
    private val encoders: Array<PacketGenerator>,
    private val logger: KimchiLogger = EmptyLogger,
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
        val body = packet.substringAfter(':')

        val packetRoute = PacketRoute(
            source = source,
            destination = destination,
            digipeaters = digipeaters,
        )

        infoParsers
            .forEach { parser ->
                try {
                    return AprsPacket(
                        route = packetRoute,
                        data = parser.parse(body),
                    )
                } catch (error: Throwable) {
                    logger.debug(error) { "${parser::class.simpleName} failed to parse: ${error.message}" }
                }
            }
        logger.warn("No parser was able to parse packet.")
        return AprsPacket(
            route = packetRoute,
            data = PacketData.Unknown(body)
        )
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

        val body = packet.drop(17 + lastDigipeater).map { it.toChar() }.toCharArray().concatToString()

        val route = PacketRoute(
            source = source,
            destination = destination,
            digipeaters = digipeaters,
        )

        infoParsers
            .forEach { parser ->
                try {
                    return AprsPacket(
                        route = route,
                        data = parser.parse(body),
                    )
                } catch (error: Throwable) {
                    logger.debug(error) { "${parser::class.simpleName} failed to parse: ${error.message}" }
                }
            }
        logger.warn("No parser was able to parse packet.")
        return AprsPacket(
            route = route,
            data = PacketData.Unknown(body)
        )
    }

    override fun toString(packet: AprsPacket): String {
        val route = arrayOf(packet.route.destination, *packet.route.digipeaters.toTypedArray()).joinToString(",")
        encoders.forEach { encoder ->
            try {
                val body = encoder.generate(packet.data)
                return "${packet.route.source}>$route:$body"
            } catch (pass: UnhandledEncodingException) {
                logger.trace { "Encoding is unhandled by <${encoder::class.simpleName}>" }
            } catch (error: Throwable) {
                logger.warn(error) { "Encoder <${encoder::class.simpleName}> threw unknown error." }
            }
        }

        throw IllegalArgumentException("No packet encoder was able to handle the given packet type.")
    }

    private fun List<UByte>.toCallsign(): String {
        return map { it.toInt() shr 1 }
            .map { it.toChar() }
            .toCharArray()
            .concatToString()
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
