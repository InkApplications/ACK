package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketRoute
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import inkapplications.spondee.measure.*
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.of

internal class PositionlessWeatherParser(
    timestampModule: TimestampModule,
): PacketTypeParser {
    private val dataType = ControlCharacterChunker('_')
    private val timestampParser = timestampModule.mdhmChunker

    private val windDirectionParser = WeatherElementChunker('c', 3)
        .mapParsed { it?.let(Degrees::of) }
    private val windSpeedParser = WeatherElementChunker('s', 3)
        .mapParsed { it?.let { MilesPerHour.of(it) } }
    private val windGustParser = WeatherElementChunker('g', 3)
        .mapParsed { it?.let { MilesPerHour.of(it) } }
    private val tempParser = WeatherElementChunker('t', 3)
        .mapParsed { it?.let { Fahrenheit.of(it) } }

    override fun parse(route: PacketRoute, body: String): AprsPacket.Weather {
        val dataType = dataType.popChunk(body)
        val timestamp = timestampParser.parseAfter(dataType)
        val windDirection = windDirectionParser.parseAfter(timestamp)
        val windSpeed = windSpeedParser.parseAfter(windDirection)
        val windGust = windGustParser.parseAfter(windSpeed)
        val temperature = tempParser.parseAfter(windGust)
        val weatherData = WeatherChunker.parseOptionalAfter(temperature)

        return AprsPacket.Weather(
            route = route,
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
