package com.inkapplications.ack.codec

import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketRoute
import com.inkapplications.ack.structures.station.StationAddress
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger

internal class AckCodec(
    private val dataParsers: Array<out PacketDataParser>,
    private val dataGenerators: Array<out PacketDataGenerator>,
    private val logger: KimchiLogger = EmptyLogger,
): AprsCodec {
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

        dataParsers.forEach { parser ->
            try {
                return AprsPacket(
                    route = packetRoute,
                    data = parser.parse(body),
                )
            } catch (error: Throwable) {
                logger.debug(error) { "${parser::class.simpleName} failed to parse: ${error.message}" }
            }
        }

        throw IllegalArgumentException("No parser was able to parse packet.")
    }

    override fun fromAx25(packet: ByteArray): AprsPacket {
        logger.trace("Parsing packet from bytes: $packet")
        val unsignedByteArray = packet.toUByteArray()

        val destination = StationAddress(
            callsign = unsignedByteArray.slice(0..5).toCallsign(),
            ssid = unsignedByteArray[6].toSsid()
        )
        val source = StationAddress(
            callsign = unsignedByteArray.slice(7..12).toCallsign(),
            ssid = unsignedByteArray[13].toSsid()
        )
        val lastDigipeater = unsignedByteArray.drop(14).indexOfFirst { it.toInt() and 1 == 1 }
        val digipeaters = unsignedByteArray
            .drop(14)
            .take(lastDigipeater + 1)
            .chunked(7)
            .map { StationAddress(it.slice(0..5).toCallsign(), it[6].toSsid()) }
            .map { Digipeater(it) }

        val body = packet.drop(17 + lastDigipeater).map { it.toInt().toChar() }.toCharArray().concatToString()

        val route = PacketRoute(
            source = source,
            destination = destination,
            digipeaters = digipeaters,
        )

        dataParsers.forEach { parser ->
            try {
                return AprsPacket(
                    route = route,
                    data = parser.parse(body),
                )
            } catch (error: Throwable) {
                logger.debug(error) { "${parser::class.simpleName} failed to parse: ${error.message}" }
            }
        }

        throw IllegalArgumentException("No parser was able to parse packet.")
    }

    override fun toString(packet: AprsPacket, config: EncodingConfig): String {
        val route = arrayOf(packet.route.destination, *packet.route.digipeaters.toTypedArray()).joinToString(",")
        dataGenerators.forEach { encoder ->
            try {
                val body = encoder.generate(packet.data, config)
                return "${packet.route.source}>$route:$body"
            } catch (pass: UnhandledEncodingException) {
                logger.trace { "Encoding is unhandled by <${encoder::class.simpleName}>" }
            } catch (error: Throwable) {
                logger.warn(error) { "Encoder <${encoder::class.simpleName}> threw unknown error." }
            }
        }

        throw IllegalArgumentException("No packet encoder was able to handle the given packet type.")
    }

    override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray {
        val destination = packet.route.destination.toBytes(hBit = true)
        val source = packet.route.source.toBytes()
        val digipeaters = packet.route.digipeaters.flatMapIndexed { index, digipeater ->
            digipeater.address.toBytes(hBit = digipeater.heard, extensionBit = index == packet.route.digipeaters.size - 1).toList()
        }.toByteArray()
        val control = 0b0000011.toByte()
        val pid = 0b11110000.toByte()

        dataGenerators.forEach { encoder ->
            try {
                val body = encoder.generate(packet.data, config)

                return destination + source + digipeaters + control + pid + body.toCharArray().map { it.code.toByte() }.toByteArray()
            } catch (pass: UnhandledEncodingException) {
                logger.trace { "Encoding is unhandled by <${encoder::class.simpleName}>" }
            } catch (error: Throwable) {
                logger.warn(error) { "Encoder <${encoder::class.simpleName}> threw unknown error." }
            }
        }

        throw IllegalArgumentException("No packet encoder was able to handle the given packet type.")

    }

    private fun StationAddress.toBytes(hBit: Boolean = false, extensionBit: Boolean = false): ByteArray {
        val callsign = callsign.canonical
            .fixedLength(6)
            .encodeToByteArray()
            .map { it.toInt() shl 1 }
            .map { it.toUByte() }
            .toUByteArray()
            .toByteArray()
        val ssid = ssid.value.toCharArray().firstOrNull()
            ?.code
            ?.shl(1)
            .let { it ?: 0 }
            .or(0b01100000)
            .or(if (hBit) 0b10000000 else 0)
            .or(if (extensionBit) 0b00000001 else 0)

        return callsign + ssid.toByte()
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

    private fun String.parseAddress() = StationAddress(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0")
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
