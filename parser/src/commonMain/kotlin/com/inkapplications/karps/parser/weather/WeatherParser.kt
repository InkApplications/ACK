package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.weather.WeatherChunkParser.DATA
import com.inkapplications.karps.parser.weather.WeatherChunkParser.ID
import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*

class WeatherParser: PacketInformationParser {
    override val dataTypeFilter = charArrayOf('_', '!', '/', '@', '=')
    val format = Regex("""^((?:${ID}${DATA})+)(.)?(.{2,4})?""")

    override fun parse(packet: AprsPacket): AprsPacket {
        val result = format.find(packet.body) ?: return packet
        val weatherData = WeatherChunkParser.getChunks(result.groupValues[1])

        val weatherPacket = AprsPacket.Weather(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = packet.timestamp,
            extension = null,
            body = packet.body.substring(result.groupValues[0].length),
            windData = WindData(
                direction = (packet.extension as? DataExtension.TrajectoryExtra)?.value?.direction ?: weatherData['c']?.degreesBearing,
                speed = (packet.extension as? DataExtension.TrajectoryExtra)?.value?.speed ?: weatherData['s']?.mph,
                gust = weatherData['g']?.mph
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData['r']?.hundredthsOfInch,
                rainLast24Hours = weatherData['p']?.hundredthsOfInch,
                rainToday = weatherData['P']?.hundredthsOfInch,
                rawRain = weatherData['#']
            ),
            temperature = weatherData['t']?.degreesFahrenheit,
            humidity = weatherData['h']?.percent,
            pressure = weatherData['b']?.decapascals,
            irradiance = weatherData['L']?.wattsPerSquareMeter ?: weatherData['l']?.plus(1000)?.wattsPerSquareMeter
        )

        return when (packet) {
            is AprsPacket.Position -> weatherPacket.copy(
                position = packet.coordinates,
                symbol = packet.symbol
            )
            else -> weatherPacket
        }
    }
}
