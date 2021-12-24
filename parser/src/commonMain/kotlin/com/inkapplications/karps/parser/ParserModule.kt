package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.capabilities.CapabilitiesTransformer
import com.inkapplications.karps.parser.item.ItemTransformer
import com.inkapplications.karps.parser.item.ObjectTransformer
import com.inkapplications.karps.parser.message.MessageTransformer
import com.inkapplications.karps.parser.position.PositionParser
import com.inkapplications.karps.parser.status.StatusReportTransformer
import com.inkapplications.karps.parser.telemetry.TelemetryTransformer
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.weather.PositionlessWeatherParser
import com.inkapplications.karps.parser.weather.WeatherParser
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.datetime.TimeZone

/**
 * Creates Parser instances.
 */
class ParserModule(
    private val logger: KimchiLogger = EmptyLogger,
    private val timezone: TimeZone = TimeZone.currentSystemDefault(),
) {
    private val timestampModule = TimestampModule(
        timezone = timezone,
    )

    fun defaultParsers(): Array<PacketDataParser> {
        return arrayOf(
            WeatherParser(timestampModule = timestampModule),
            PositionlessWeatherParser(timestampModule = timestampModule),
        )
    }
    fun defaultGenerators(): Array<PacketDataGenerator> {
        return arrayOf()
    }
    fun defaultTransformers(): Array<PacketDataTransformer> {
        return arrayOf(
            PositionParser(timestampModule = timestampModule),
            CapabilitiesTransformer(),
            ObjectTransformer(timestampModule),
            ItemTransformer(),
            MessageTransformer(),
            TelemetryTransformer(),
            StatusReportTransformer(timestampModule = timestampModule),
            UnknownPacketTransformer,
        )
    }

    fun parser(
        infoParsers: Array<PacketDataParser>,
        encoders: Array<PacketDataGenerator>,
        logger: KimchiLogger = EmptyLogger
    ): AprsParser = KarpsParser(infoParsers, encoders = encoders, logger = logger)

    /**
     * Create a standard packet parser with the default parsing modules.
     */
    fun defaultParser(): AprsParser {
        val parsers = defaultParsers()
        val encoders = defaultGenerators()
        val transformers = defaultTransformers()

        return parser(
            infoParsers = parsers + transformers,
            encoders = encoders + transformers,
            logger = logger
        )
    }
}
