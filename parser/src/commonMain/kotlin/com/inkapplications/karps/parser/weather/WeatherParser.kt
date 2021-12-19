package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.common.CompositeChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.TrajectoryExtensionChunker
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.PositionReport
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.PacketData
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import inkapplications.spondee.measure.*
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.of

internal class WeatherParser(
    timestampModule: TimestampModule,
): PacketTypeParser {
    private val dataTypeChunker = ControlCharacterChunker('!', '/', '@', '=')
    private val timestampParser = CompositeChunker(
        timestampModule.dhmlChunker,
        timestampModule.dhmzChunker,
        timestampModule.hmsChunker,
    )

    override fun parse(body: String): PacketData.Weather {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val timestamp = timestampParser.parseOptionalAfter(dataTypeIdentifier)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedWind = position.result.compressedExtension
        val plainWindExtension = if (position.result is PositionReport.Plain) TrajectoryExtensionChunker.parseAfter(position) else null
        val windData = when {
            plainWindExtension != null -> plainWindExtension.result.value
            compressedWind != null -> compressedWind.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)!!
            else -> null
        }
        val weatherData = WeatherChunker.parseAfter(plainWindExtension ?: position)

        return PacketData.Weather(
            timestamp = timestamp.result,
            coordinates = position.result.coordinates,
            symbol = position.result.symbol,
            windData = WindData(
                direction = windData?.direction,
                speed = windData?.speed,
                gust = weatherData.result['g']?.let { MilesPerHour.of(it) }
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData.result['r']?.let { HundredthInches.of(it) },
                rainLast24Hours = weatherData.result['p']?.let { HundredthInches.of(it) },
                rainToday = weatherData.result['P']?.let { HundredthInches.of(it) },
                rawRain = weatherData.result['#']
            ),
            temperature = weatherData.result['t']?.let { Fahrenheit.of(it) },
            humidity = weatherData.result['h']?.let { WholePercentage.of(it) },
            pressure = weatherData.result['b']?.let { Pascals.of(Deka, it) },
            irradiance = weatherData.result['L']?.let { WattsPerSquareMeter.of(it) }
                ?: weatherData.result['l']?.plus(1000)?.let { WattsPerSquareMeter.of(it) }
        )
    }
}
