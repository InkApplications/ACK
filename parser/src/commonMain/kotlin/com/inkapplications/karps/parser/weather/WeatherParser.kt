package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketDataTransformer
import com.inkapplications.karps.parser.chunk.common.CompositeChunker
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.extension.TrajectoryExtensionChunker
import com.inkapplications.karps.parser.format.fixedLength
import com.inkapplications.karps.parser.position.*
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.PositionReport
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.requireType
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.*
import inkapplications.spondee.measure.*
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.structure.Deka
import inkapplications.spondee.structure.of
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

internal class WeatherParser(
    private val timestampModule: TimestampModule,
): PacketDataTransformer {
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
                rawRain = weatherData.result['#'],
                snowLast24Hours = weatherData.result['s']?.let { Inches.of(it) },
            ),
            temperature = weatherData.result['t']?.let { Fahrenheit.of(it) },
            humidity = weatherData.result['h']?.let { WholePercentage.of(it) },
            pressure = weatherData.result['b']?.let { Pascals.of(Deka, it) },
            irradiance = weatherData.result['L']?.let { WattsPerSquareMeter.of(it) }
                ?: weatherData.result['l']?.plus(1000)?.let { WattsPerSquareMeter.of(it) }
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Weather>()
        val coordinates = packet.coordinates ?: throw UnsupportedOperationException()
        val symbol = packet.symbol ?: throw UnsupportedOperationException()
        val identifier = when {
            packet.timestamp != null -> '/'
            else -> '!'
        }
        val time = packet.timestamp?.let(timestampModule.dhmzCodec::encode).orEmpty()
        val position = PositionCodec.encodeBody(
            config = config,
            coordinates = coordinates,
            symbol = symbol,
            windData = packet.windData,
        )
        val g = packet.windData.gust?.value(MilesPerHour).generateChunk('g')
        val t = packet.temperature?.value(Fahrenheit).generateChunk('t')
        val r = packet.precipitation.rainLastHour?.value(HundredthInches).generateChunk('r')
        val p = packet.precipitation.rainLast24Hours?.value(HundredthInches).generateChunk('p')
        val P = packet.precipitation.rainToday?.value(HundredthInches).generateChunk('P')
        val h = packet.humidity?.value(WholePercentage).generateChunk('h', 2)
        val b = packet.pressure?.value(Deka, Pascals).generateChunk('b', 5)
        val L = packet.irradiance?.value(WattsPerSquareMeter)?.takeIf { it.roundToInt() < 1000 }.generateChunk('L')
        val l = packet.irradiance?.value(WattsPerSquareMeter)?.takeIf { it.roundToInt() >= 1000 }?.minus(1000).generateChunk('l')
        val sn = packet.precipitation.snowLast24Hours?.value(Inches).generateChunk('s')
        val rawRain = packet.precipitation.rawRain.generateChunk('#')

        return "$identifier$time$position$g$t$r$p$P$h$b$L$l$sn$rawRain"
    }

    private fun Number?.generateChunk(symbol: Char, size: Int = 3): String {
        return this?.toDouble()
            ?.roundToInt()
            ?.fixedLength(size)
            ?.let { "$symbol$it" }
            .orEmpty()
    }
}
