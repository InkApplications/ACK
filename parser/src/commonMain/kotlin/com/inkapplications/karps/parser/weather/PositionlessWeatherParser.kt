package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.timestamp.TIMESTAMP
import com.inkapplications.karps.structures.AprsPacket

/**
 * Parse Positionless weather packets.
 *
 * Per specification, wind direction, wind speed, gust and temperature are
 * required fields at the start of this packet type.
 * The specification for this packet type is particularly unclear about the
 * station identifiers at the end of the packet. It is not clear if the APRS
 * software character is required, however it's assumed to be required in this
 * parser or it would make the Wx unit type field unparsable.
 */
class PositionlessWeatherParser: PacketInformationParser {
    private val format = Regex("""(${TIMESTAMP})(c(\d{3}|\.{3}|\s{3}))(s(\d{3}|\.{3}|\s{3}))(g(\d{3}|\.{3}|\s{3}))(${TemperatureParser.regex.pattern})([a-zA-Z]\d{2,5})*(.)(.{2,4})$""")
    override val supportedDataTypes: CharArray = charArrayOf('_')

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val results = format.find(packet.body) ?: throw PacketFormatException("Not a weather packet.")

        return AprsPacket.Weather(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            temperature = TemperatureParser.parse(results.groupValues[8])
        )
    }
}

