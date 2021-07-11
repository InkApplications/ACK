package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.*
import com.inkapplications.karps.parser.timestamp.MdhmChunker
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.*
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.of

class PositionlessWeatherParser: PacketTypeParser {
    override val dataTypeFilter: CharArray? = charArrayOf('_')
    private val timestampParser = MdhmChunker()

    private val windDirectionParser = WeatherElementChunker('c', 3)
        .mapParsed { it?.let(Degrees::of) }
    private val windSpeedParser = WeatherElementChunker('s', 3)
        .mapParsed { it?.let { MilesPerHour.of(it) } }
    private val windGustParser = WeatherElementChunker('g', 3)
        .mapParsed { it?.let { MilesPerHour.of(it) } }
    private val tempParser = WeatherElementChunker('t', 3)
        .mapParsed { it?.let { Fahrenheit.of(it) } }

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
            humidity = weatherData.result?.get('h')?.let { WholePercentage.of(it) },
            pressure = weatherData.result?.get('b')?.let { Pascals.of(Deka, it) },
            irradiance = weatherData.result?.get('L')?.let { WattsPerSquareMeter.of(it) }
                ?: weatherData.result?.get('l')?.plus(1000)?.let { WattsPerSquareMeter.of(it) }
        )
    }
}
