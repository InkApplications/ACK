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

        val startingInfo = PacketInformation(dataType, body)
        logger.trace("Parsing Information with ${infoParsers.size} parsers starting with: $startingInfo")
        val packetInformation = infoParsers.fold(startingInfo) { info, parser ->
            if (parser.dataTypeFilter?.contains(info.dataType) == false) {
                logger.trace { "Skipping ${parser::class.simpleName}" }
                info
            } else {
                logger.trace { "Running ${parser::class.simpleName}" }
                parser.parse(info)
            }
        }

        logger.debug("Parsed Information: $packetInformation")

        return when {
            packetInformation.dataType in charArrayOf('_', '!', '/', '@', '=')
                && (packetInformation.symbol?.id == '_' || packetInformation.dataType == '_')
                && packetInformation.windData != null
                && packetInformation.precipitation != null ->
                AprsPacket.Weather(
                    received = clock.current,
                    dataTypeIdentifier = packetInformation.dataType,
                    source = source,
                    destination = destination,
                    digipeaters = digipeaters,
                    symbol = packetInformation.symbol,
                    timestamp = packetInformation.timestamp,
                    position = packetInformation.position,
                    windData = packetInformation.windData,
                    precipitation = packetInformation.precipitation,
                    temperature = packetInformation.temperature,
                    humidity = packetInformation.humidity,
                    pressure = packetInformation.pressure,
                    irradiance = packetInformation.irradiance
                )
            packetInformation.dataType in charArrayOf('!', '/', '@', '=')
            && packetInformation.position != null
            && packetInformation.symbol != null ->
                AprsPacket.Position(
                    received = clock.current,
                    dataTypeIdentifier = packetInformation.dataType,
                    source = source,
                    destination = destination,
                    digipeaters = digipeaters,
                    coordinates = packetInformation.position,
                    symbol = packetInformation.symbol,
                    comment = packetInformation.body,
                    extension = packetInformation.extension,
                    timestamp = null
                )
            else -> AprsPacket.Unknown(
                received = clock.current,
                dataTypeIdentifier = packetInformation.dataType,
                source = source,
                destination = destination,
                digipeaters = digipeaters,
                body = packetInformation.toString()
            )
        }
    }

    private fun String.parseAddress() = Address(
        callsign = substringBefore('-'),
        ssid = substringAfter('-', "0")
    )

    private fun String.charAfter(other: Char) = this[indexOf(other) + 1]
}
