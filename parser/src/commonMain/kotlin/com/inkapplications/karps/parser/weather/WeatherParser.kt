package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.common.CompositeChunker
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptional
import com.inkapplications.karps.parser.extension.TrajectoryExtensionChunker
import com.inkapplications.karps.parser.position.CompressedPositionExtensions
import com.inkapplications.karps.parser.position.MixedPositionChunker
import com.inkapplications.karps.parser.position.PositionReport
import com.inkapplications.karps.parser.position.compressedExtension
import com.inkapplications.karps.parser.timestamp.*
import com.inkapplications.karps.parser.valueFor
import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset

class WeatherParser(
    timezone: TimezoneOffset = TimezoneOffset.local(DateTime.now())
): PacketTypeParser {
    override val dataTypeFilter = charArrayOf('!', '/', '@', '=')
    private val timestampParser = CompositeChunker(
        DhmlChunker(timezone),
        DhmzChunker(),
        HmsChunker()
    )

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.Weather {
        val timestamp = timestampParser.parseOptional(packet)
        val position = MixedPositionChunker.parseAfter(timestamp)
        val compressedWind = position.result.compressedExtension
        val plainWindExtension = if (position.result is PositionReport.Plain) TrajectoryExtensionChunker.parseAfter(position) else null
        val windData = when {
            plainWindExtension != null -> plainWindExtension.result.value
            compressedWind != null -> compressedWind.valueFor(CompressedPositionExtensions.TrajectoryExtra::class)!!
            else -> null
        }
        val weatherData = WeatherChunker.parseAfter(plainWindExtension ?: position)

        return AprsPacket.Weather(
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            timestamp = timestamp.result,
            coordinates = position.result.coordinates,
            symbol = position.result.symbol,
            windData = WindData(
                direction = windData?.direction,
                speed = windData?.speed,
                gust = weatherData.result['g']?.mph
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData.result['r']?.hundredthsOfInch,
                rainLast24Hours = weatherData.result['p']?.hundredthsOfInch,
                rainToday = weatherData.result['P']?.hundredthsOfInch,
                rawRain = weatherData.result['#']
            ),
            temperature = weatherData.result['t']?.degreesFahrenheit,
            humidity = weatherData.result['h']?.percent,
            pressure = weatherData.result['b']?.decapascals,
            irradiance = weatherData.result['L']?.wattsPerSquareMeter ?: weatherData.result['l']?.plus(1000)?.wattsPerSquareMeter
        )
    }
}
