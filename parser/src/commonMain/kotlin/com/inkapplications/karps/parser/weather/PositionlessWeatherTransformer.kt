package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketDataTransformer
import com.inkapplications.karps.parser.UnhandledEncodingException
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.format.fixedLength
import com.inkapplications.karps.parser.requireType
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import inkapplications.spondee.measure.*
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.of
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

internal class PositionlessWeatherTransformer(
    private val timestampModule: TimestampModule,
): PacketDataTransformer {
    private val dataTypeIdentifier = '_'
    private val dataType = ControlCharacterChunker(dataTypeIdentifier)
    private val timestampParser = timestampModule.mdhmChunker

    private val windDirectionParser = WeatherElementChunker('c', 3)
        .mapParsed { it?.let(Degrees::of) }
    private val windSpeedParser = WeatherElementChunker('s', 3)
        .mapParsed { it?.let { MilesPerHour.of(it) } }
    private val windGustParser = WeatherElementChunker('g', 3)
        .mapParsed { it?.let { MilesPerHour.of(it) } }
    private val tempParser = WeatherElementChunker('t', 3)
        .mapParsed { it?.let { Fahrenheit.of(it) } }

    override fun parse(body: String): PacketData.Weather {
        val dataType = dataType.popChunk(body)
        val timestamp = timestampParser.parseAfter(dataType)
        val windDirection = windDirectionParser.parseAfter(timestamp)
        val windSpeed = windSpeedParser.parseAfter(windDirection)
        val windGust = windGustParser.parseAfter(windSpeed)
        val temperature = tempParser.parseAfter(windGust)
        val weatherData = WeatherChunker.parseOptionalAfter(temperature)

        return PacketData.Weather(
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
                rawRain = weatherData.result?.get('#'),
                snowLast24Hours = weatherData.result?.get('s')?.let { Inches.of(it) },
            ),
            temperature = temperature.result,
            humidity = weatherData.result?.get('h')?.let { WholePercentage.of(it) },
            pressure = weatherData.result?.get('b')?.let { Pascals.of(Deka, it) },
            irradiance = weatherData.result?.get('L')?.let { WattsPerSquareMeter.of(it) }
                ?: weatherData.result?.get('l')?.plus(1000)?.let { WattsPerSquareMeter.of(it) }
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Weather>()
        if (packet.coordinates != null) throw UnhandledEncodingException()
        val fill = config.weatherDataFillCharacter
        val time = packet.timestamp?.let(timestampModule.mdhmCodec::encode).orEmpty()

        val c = packet.windData.direction?.value(Degrees).generateChunk('c', fill)
        val s = packet.windData.speed?.value(MilesPerHour).generateChunk('s', fill)
        val g = packet.windData.gust?.value(MilesPerHour).generateChunk('g', fill)
        val t = packet.temperature?.value(Fahrenheit).generateChunk('t', fill)
        val r = packet.precipitation.rainLastHour?.value(HundredthInches)?.generateChunk('r', fill).orEmpty()
        val p = packet.precipitation.rainLast24Hours?.value(HundredthInches)?.generateChunk('p', fill).orEmpty()
        val P = packet.precipitation.rainToday?.value(HundredthInches)?.generateChunk('P', fill).orEmpty()
        val h = packet.humidity?.value(WholePercentage)?.generateChunk('h', fill, 2).orEmpty()
        val b = packet.pressure?.value(Deka, Pascals)?.generateChunk('b', fill, 5).orEmpty()
        val L = packet.irradiance?.value(WattsPerSquareMeter)?.takeIf { it.roundToInt() < 1000 }?.generateChunk('L', fill).orEmpty()
        val l = packet.irradiance?.value(WattsPerSquareMeter)?.takeIf { it.roundToInt() >= 1000 }?.minus(1000)?.generateChunk('l', fill).orEmpty()
        val sn = packet.precipitation.snowLast24Hours?.value(Inches)?.generateChunk('s', fill).orEmpty()
        val rawRain = packet.precipitation.rawRain?.generateChunk('#', fill).orEmpty()

        return "$dataTypeIdentifier$time$c$s$g$t$r$p$P$h$b$L$l$sn$rawRain"
    }

    private fun Number?.generateChunk(symbol: Char, fill: Char, size: Int = 3): String {
        val value = this?.toDouble()?.roundToInt()?.fixedLength(size) ?: fill.toString().repeat(size)

        return "$symbol$value"
    }
}
