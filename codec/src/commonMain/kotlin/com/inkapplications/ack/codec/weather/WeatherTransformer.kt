package com.inkapplications.ack.codec.weather

import com.inkapplications.ack.codec.PacketDataTransformer
import com.inkapplications.ack.codec.chunk.common.CompositeChunker
import com.inkapplications.ack.codec.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.codec.chunk.parseAfter
import com.inkapplications.ack.codec.chunk.parseOptionalAfter
import com.inkapplications.ack.codec.extension.TrajectoryExtensionChunker
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.codec.position.*
import com.inkapplications.ack.codec.requireType
import com.inkapplications.ack.codec.timestamp.TimestampModule
import com.inkapplications.ack.codec.valueFor
import com.inkapplications.ack.structures.*
import inkapplications.spondee.measure.*
import inkapplications.spondee.measure.metric.pascals
import inkapplications.spondee.measure.metric.toWattsPerSquareMeter
import inkapplications.spondee.measure.metric.wattsPerSquareMeter
import inkapplications.spondee.measure.us.*
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.structure.*
import kotlin.math.roundToInt

internal class WeatherTransformer(
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
                gust = weatherData.result['g']?.milesPerHour,
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData.result['r']?.scale(Hundreths)?.inches,
                rainLast24Hours = weatherData.result['p']?.scale(Hundreths)?.inches,
                rainToday = weatherData.result['P']?.scale(Hundreths)?.inches,
                rawRain = weatherData.result['#'],
                snowLast24Hours = weatherData.result['s']?.inches,
            ),
            temperature = weatherData.result['t']?.fahrenheit,
            humidity = weatherData.result['h']?.percent,
            pressure = weatherData.result['b']?.scale(Deka)?.pascals,
            irradiance = weatherData.result['L']?.wattsPerSquareMeter
                ?: weatherData.result['l']?.plus(1000)?.wattsPerSquareMeter,
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
        val g = packet.windData.gust?.toMilesPerHourValue().generateChunk('g')
        val t = packet.temperature?.toFahrenheit()?.value.generateChunk('t')
        val r = packet.precipitation.rainLastHour?.toInches()?.value(Hundreths).generateChunk('r')
        val p = packet.precipitation.rainLast24Hours?.toInches()?.value(Hundreths).generateChunk('p')
        val P = packet.precipitation.rainToday?.toInches()?.value(Hundreths).generateChunk('P')
        val h = packet.humidity?.toWholePercentage()?.value.generateChunk('h', 2)
        val b = packet.pressure?.toPascals()?.value(Deka).generateChunk('b', 5)
        val L = packet.irradiance?.toWattsPerSquareMeter()?.value()?.takeIf { it.roundToInt() < 1000 }.generateChunk('L')
        val l = packet.irradiance?.toWattsPerSquareMeter()?.value()?.takeIf { it.roundToInt() >= 1000 }?.minus(1000).generateChunk('l')
        val sn = packet.precipitation.snowLast24Hours?.toInches()?.value.generateChunk('s')
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
