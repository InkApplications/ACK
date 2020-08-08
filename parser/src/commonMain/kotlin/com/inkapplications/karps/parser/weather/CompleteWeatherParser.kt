package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.position.PositionDataParser
import com.inkapplications.karps.parser.timestamp.TIMESTAMP
import com.inkapplications.karps.parser.timestamp.TimestampParser
import com.inkapplications.karps.parser.weather.WeatherChunkParser.DATA
import com.inkapplications.karps.parser.weather.WeatherChunkParser.ID
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.unit.*

class CompleteWeatherParser(
    private val timestampParser: TimestampParser
): PacketInformationParser {
    override val supportedDataTypes: CharArray = charArrayOf('!', '=', '/', '@')
    val format = Regex("""(${TIMESTAMP})?(${PositionDataParser.format.pattern})(?:()|(\d{3}|\.{3})/(\d{3}|\.{3}))((?:${ID}${DATA})*)(.)?(.{2,4})?""")

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        val result = format.matchEntire(packet.body) ?: throw PacketFormatException("Not a complete weather packet")

        val timestamp = runCatching { timestampParser.parse(result.groupValues[1]) }.getOrNull()
        val positionData = PositionDataParser.parse(result.groupValues[2])
        val data = WeatherChunkParser.getChunks(result.groupValues[result.groupValues.size - 3])

        return AprsPacket.Weather(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            windData = WindData(
                direction = result.groupValues[result.groupValues.size - 5].toIntOrNull()?.degreesBearing,
                speed = result.groupValues[result.groupValues.size - 4].toIntOrNull()?.mph,
                gust = data['g']?.mph
            ),
            precipitation = Precipitation(
                rainLastHour = data['r']?.hundredthsOfInch,
                rainLast24Hours = data['p']?.hundredthsOfInch,
                rainToday = data['P']?.hundredthsOfInch,
                rawRain = data['#']
            ),
            temperature = data['t']?.degreesFahrenheit,
            humidity = data['h']?.percent,
            pressure = data['b']?.decapascals,
            irradiance = data['L']?.wattsPerSquareMeter ?: data['l']?.plus(1000)?.wattsPerSquareMeter,
            timestamp = timestamp,
            coordinates = positionData.coordinates,
            symbol = positionData.symbol
        )
    }
}
