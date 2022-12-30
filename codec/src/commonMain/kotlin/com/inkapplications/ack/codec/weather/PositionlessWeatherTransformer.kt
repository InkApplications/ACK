package com.inkapplications.ack.codec.weather

import com.inkapplications.ack.codec.PacketDataTransformer
import com.inkapplications.ack.codec.UnhandledEncodingException
import com.inkapplications.ack.codec.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.codec.chunk.mapParsed
import com.inkapplications.ack.codec.chunk.parseAfter
import com.inkapplications.ack.codec.chunk.parseOptionalAfter
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.codec.requireType
import com.inkapplications.ack.codec.timestamp.TimestampModule
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.Precipitation
import com.inkapplications.ack.structures.WindData
import inkapplications.spondee.measure.*
import inkapplications.spondee.measure.metric.*
import inkapplications.spondee.measure.us.*
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.*
import kotlin.math.roundToInt

internal class PositionlessWeatherTransformer(
    private val timestampModule: TimestampModule,
): PacketDataTransformer {
    private val dataTypeIdentifier = '_'
    private val dataType = ControlCharacterChunker(dataTypeIdentifier)
    private val timestampParser = timestampModule.mdhmChunker

    private val windDirectionParser = WeatherElementChunker('c', 3)
        .mapParsed { it?.degrees }
    private val windSpeedParser = WeatherElementChunker('s', 3)
        .mapParsed { it?.milesPerHour }
    private val windGustParser = WeatherElementChunker('g', 3)
        .mapParsed { it?.milesPerHour }
    private val tempParser = WeatherElementChunker('t', 3)
        .mapParsed { it?.fahrenheit }

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
                rainLastHour = weatherData.result?.get('r')?.scale(Hundreths)?.inches,
                rainLast24Hours = weatherData.result?.get('p')?.scale(Hundreths)?.inches,
                rainToday = weatherData.result?.get('P')?.scale(Hundreths)?.inches,
                rawRain = weatherData.result?.get('#'),
                snowLast24Hours = weatherData.result?.get('s')?.inches,
            ),
            temperature = temperature.result,
            humidity = weatherData.result?.get('h')?.percent,
            pressure = weatherData.result?.get('b')?.scale(Deka)?.pascals,
            irradiance = (weatherData.result?.get('L') ?: weatherData.result?.get('l')?.plus(1000))?.wattsPerSquareMeter
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Weather>()
        if (packet.coordinates != null) throw UnhandledEncodingException()
        val fill = config.weatherDataFillCharacter
        val time = packet.timestamp?.let(timestampModule.mdhmCodec::encode).orEmpty()

        val c = packet.windData.direction?.toDegrees()?.value.generateChunk('c', fill)
        val s = packet.windData.speed
            ?.toMilesPerHourValue()
            .generateChunk('s', fill)
        val g = packet.windData.gust?.toMilesPerHourValue().generateChunk('g', fill)
        val t = packet.temperature?.toFahrenheit()?.value.generateChunk('t', fill)
        val r = packet.precipitation.rainLastHour?.toInches()?.value(Hundreths)?.generateChunk('r', fill).orEmpty()
        val p = packet.precipitation.rainLast24Hours?.toInches()?.value(Hundreths)?.generateChunk('p', fill).orEmpty()
        val P = packet.precipitation.rainToday?.toInches()?.value(Hundreths)?.generateChunk('P', fill).orEmpty()
        val h = packet.humidity?.toWholePercentage()?.value?.generateChunk('h', fill, 2).orEmpty()
        val b = packet.pressure?.toPascals()?.value(Deka)?.generateChunk('b', fill, 5).orEmpty()
        val L = packet.irradiance?.toWattsPerSquareMeter()?.value()?.takeIf { it.roundToInt() < 1000 }?.generateChunk('L', fill).orEmpty()
        val l = packet.irradiance?.toWattsPerSquareMeter()?.value()?.takeIf { it.roundToInt() >= 1000 }?.minus(1000)?.generateChunk('l', fill).orEmpty()
        val sn = packet.precipitation.snowLast24Hours?.toInches()?.value?.generateChunk('s', fill).orEmpty()
        val rawRain = packet.precipitation.rawRain?.generateChunk('#', fill).orEmpty()

        return "$dataTypeIdentifier$time$c$s$g$t$r$p$P$h$b$L$l$sn$rawRain"
    }

    private fun Number?.generateChunk(symbol: Char, fill: Char, size: Int = 3): String {
        val value = this?.toDouble()?.roundToInt()?.fixedLength(size) ?: fill.toString().repeat(size)

        return "$symbol$value"
    }
}
