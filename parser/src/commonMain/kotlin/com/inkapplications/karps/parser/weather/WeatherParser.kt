package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketFormatException
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
        val compressedWind = position.parsed.compressedExtension
        val plainWindExtension = if (position.parsed is PositionReport.Plain) TrajectoryExtensionChunker.parseAfter(position) else null
        val windData = when {
            plainWindExtension != null -> plainWindExtension.parsed.value
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
            timestamp = timestamp.parsed,
            position = position.parsed.coordinates,
            symbol = position.parsed.symbol,
            windData = WindData(
                direction = windData?.direction,
                speed = windData?.speed,
                gust = weatherData.parsed['g']?.mph
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData.parsed['r']?.hundredthsOfInch,
                rainLast24Hours = weatherData.parsed['p']?.hundredthsOfInch,
                rainToday = weatherData.parsed['P']?.hundredthsOfInch,
                rawRain = weatherData.parsed['#']
            ),
            temperature = weatherData.parsed['t']?.degreesFahrenheit,
            humidity = weatherData.parsed['h']?.percent,
            pressure = weatherData.parsed['b']?.decapascals,
            irradiance = weatherData.parsed['L']?.wattsPerSquareMeter ?: weatherData.parsed['l']?.plus(1000)?.wattsPerSquareMeter
        )
    }
}
