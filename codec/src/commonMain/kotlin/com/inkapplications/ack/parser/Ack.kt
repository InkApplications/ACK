package com.inkapplications.ack.parser

import com.inkapplications.ack.parser.capabilities.CapabilitiesTransformer
import com.inkapplications.ack.parser.item.ItemTransformer
import com.inkapplications.ack.parser.item.ObjectTransformer
import com.inkapplications.ack.parser.message.MessageTransformer
import com.inkapplications.ack.parser.position.PositionTransformer
import com.inkapplications.ack.parser.status.StatusReportTransformer
import com.inkapplications.ack.parser.telemetry.TelemetryTransformer
import com.inkapplications.ack.parser.timestamp.TimestampModule
import com.inkapplications.ack.parser.weather.PositionlessWeatherTransformer
import com.inkapplications.ack.parser.weather.WeatherTransformer
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

/**
 * Creates Parser instances.
 *
 * @param logger Used for logging parsing information and non-fatal errors.
 * @param timezone Overrides the timezone used by time based codecs.
 * @param clock Overrides the current time for time based codecs.
 */
class Ack(
    private val logger: KimchiLogger = EmptyLogger,
    timezone: TimeZone = TimeZone.currentSystemDefault(),
    clock: Clock = Clock.System,
) {
    private val timestampModule = TimestampModule(
        timezone = timezone,
        clock = clock,
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
    ): AprsCodec = AckCodec(dataParsers, dataGenerators, logger)

    /**
     * Create a standard packet parser with the default parsing modules.
     */
    fun defaultParser(): AprsCodec {
        val transformers = defaultTransformers()

        return parser(
            dataParsers = transformers,
            dataGenerators = transformers,
        )
    }
}
