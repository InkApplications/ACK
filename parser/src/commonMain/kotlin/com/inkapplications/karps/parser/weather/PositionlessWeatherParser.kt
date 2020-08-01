package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.timestamp.TIMESTAMP
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.unit.*

private const val CHUNK = """(?:\d{2,5}|\.{2,5})"""

/**
 * Parse Positionless weather packets.
 *
 * Per specification, wind direction, wind speed, gust and temperature are
 * required fields at the start of this packet type.
 * The specification for this packet type is particularly unclear about the
 * station identifiers at the end of the packet. There is chunk ambiguity
 * when there are 3-4 characters at the end of the packet, as this could be
 * a software identifier and 2-3 WX unit characters or a single 3-4 character
 * WX identifier.
 * For the purposes of this parser, the APRS software character is greedy and
 * will take precedence over the WX Identifier. For example:
 * A packet may end in the characters `xyz` and this will be parsed as software
 * `x` and WX identifier `yz`.
 * If there is a more correct way to parse this ambiguity, please open an issue
 * on the project page.
 */
class PositionlessWeatherParser: PacketInformationParser {
    private val format = Regex("""^(${TIMESTAMP})(c${CHUNK}s${CHUNK}g${CHUNK}t${CHUNK}(?:[a-zA-Z]${CHUNK})*)(.)?(.{2,4})?$""")
    override val supportedDataTypes: CharArray = charArrayOf('_')

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val results = format.find(packet.body) ?: throw PacketFormatException("Not a weather packet.")
        val data = WeatherChunkParser.getChunks(results.groupValues[2])

        return AprsPacket.Weather(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            windData = WindData(
                direction = data['c']?.degreesBearing,
                speed = data['s']?.mph,
                gust = data['g']?.mph
            ),
            precipitation = Precipitation(
                rainLastHour = data['r']?.hundredthsOfInch,
                rainLast24Hours = data['p']?.hundredthsOfInch,
                rainToday = data['P']?.hundredthsOfInch
            ),
            temperature = data['t']?.degreesFahrenheit,
            humidity = data['h']?.percent,
            pressure = data['b']?.decapascals,
            irradiance = data['L']?.wattsPerSquareMeter ?: data['l']?.plus(1000)?.wattsPerSquareMeter
        )
    }
}

