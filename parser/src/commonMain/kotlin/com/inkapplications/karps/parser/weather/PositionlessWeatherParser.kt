package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.*
import com.inkapplications.karps.parser.timestamp.MdhmChunker
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.unit.*

class PositionlessWeatherParser: PacketTypeParser {
    override val dataTypeFilter: CharArray? = charArrayOf('_')
    private val timestampParser = MdhmChunker()

    private val windDirectionParser = WeatherElementChunker('c', 3)
        .mapParsed { it?.degreesBearing }
    private val windSpeedParser = WeatherElementChunker('s', 3)
        .mapParsed { it?.mph }
    private val windGustParser = WeatherElementChunker('g', 3)
        .mapParsed { it?.mph }
    private val tempParser = WeatherElementChunker('t', 3)
        .mapParsed { it?.degreesFahrenheit }

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.Weather {
        val timestamp = timestampParser.parse(packet)
        val windDirection = windDirectionParser.parseAfter(timestamp)
        val windSpeed = windSpeedParser.parseAfter(windDirection)
        val windGust = windGustParser.parseAfter(windSpeed)
        val temperature = tempParser.parseAfter(windGust)
        val weatherData = WeatherChunker.parseOptionalAfter(temperature)

        return AprsPacket.Weather(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = timestamp.parsed,
            position = null,
            symbol = null,
            windData = WindData(
                direction = windDirection.parsed,
                speed = windSpeed.parsed,
                gust = windGust.parsed
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData.parsed?.get('r')?.hundredthsOfInch,
                rainLast24Hours = weatherData.parsed?.get('p')?.hundredthsOfInch,
                rainToday = weatherData.parsed?.get('P')?.hundredthsOfInch,
                rawRain = weatherData.parsed?.get('#')
            ),
            temperature = temperature.parsed,
            humidity = weatherData.parsed?.get('h')?.percent,
            pressure = weatherData.parsed?.get('b')?.decapascals,
            irradiance = weatherData.parsed?.get('L')?.wattsPerSquareMeter ?: weatherData.parsed?.get('l')?.plus(1000)?.wattsPerSquareMeter
        )
    }
}
