package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.capabilities.CapabilitiesTransformer
import com.inkapplications.karps.parser.item.ItemTransformer
import com.inkapplications.karps.parser.item.ObjectTransformer
import com.inkapplications.karps.parser.message.MessageTransformer
import com.inkapplications.karps.parser.position.PositionTransformer
import com.inkapplications.karps.parser.status.StatusReportTransformer
import com.inkapplications.karps.parser.telemetry.TelemetryTransformer
import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.parser.weather.PositionlessWeatherTransformer
import com.inkapplications.karps.parser.weather.WeatherTransformer
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.datetime.TimeZone

/**
 * Creates Parser instances.
 */
class ParserModule(
    private val logger: KimchiLogger = EmptyLogger,
    timezone: TimeZone = TimeZone.currentSystemDefault(),
) {
    private val timestampModule = TimestampModule(
        timezone = timezone,
    )

    /**
     * Create the collection of built-in Data Transformer classes.
     */
    fun defaultTransformers(): Array<PacketDataTransformer> {
        return arrayOf(
            WeatherTransformer(timestampModule = timestampModule),
            PositionlessWeatherTransformer(timestampModule = timestampModule),
            PositionTransformer(timestampModule = timestampModule),
            CapabilitiesTransformer(),
            ObjectTransformer(timestampModule),
            ItemTransformer(),
            MessageTransformer(),
            TelemetryTransformer(),
            StatusReportTransformer(timestampModule = timestampModule),
            UnknownPacketTransformer,
        )
    }

    /**
     * Create a parser with specific parameters.
     */
    fun parser(
        dataParsers: Array<out PacketDataParser>,
        dataGenerators: Array<out PacketDataGenerator>,
    ): AprsParser = KarpsParser(dataParsers, dataGenerators, logger)

    /**
     * Create a standard packet parser with the default parsing modules.
     */
    fun defaultParser(): AprsParser {
        val transformers = defaultTransformers()

        return parser(
            dataParsers = transformers,
            dataGenerators = transformers,
        )
    }
}
