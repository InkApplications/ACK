package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.*
import com.inkapplications.karps.parser.timestamp.MdhmChunker
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.HundredthInches
import inkapplications.spondee.spatial.Degrees

class PositionlessWeatherParser: PacketTypeParser {
    override val dataTypeFilter: CharArray? = charArrayOf('_')
    private val timestampParser = MdhmChunker()

    private val windDirectionParser = WeatherElementChunker('c', 3)
        .mapParsed { it?.let(Degrees::of) }
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
            timestamp = timestamp.result,
            coordinates = null,
            symbol = null,
            windData = WindData(
                direction = windDirection.result,
                speed = windSpeed.result,
                gust = windGust.result
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData.result?.get('r')?.let { HundredthInches.of(it) },
                rainLast24Hours = weatherData.result?.get('p')?.let { HundredthInches.of(it) },
                rainToday = weatherData.result?.get('P')?.let { HundredthInches.of(it) },
                rawRain = weatherData.result?.get('#')
            ),
            temperature = temperature.result,
            humidity = weatherData.result?.get('h')?.percent,
            pressure = weatherData.result?.get('b')?.decapascals,
            irradiance = weatherData.result?.get('L')?.wattsPerSquareMeter ?: weatherData.result?.get('l')?.plus(1000)?.wattsPerSquareMeter
        )
    }
}
