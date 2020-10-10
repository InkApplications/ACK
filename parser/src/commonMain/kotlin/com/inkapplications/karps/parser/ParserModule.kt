package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.item.ItemParser
import com.inkapplications.karps.parser.item.ObjectParser
import com.inkapplications.karps.parser.message.MessageParser
import com.inkapplications.karps.parser.position.PositionParser
import com.inkapplications.karps.parser.weather.PositionlessWeatherParser
import com.inkapplications.karps.parser.weather.WeatherParser
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.datetime.TimeZone

/**
 * Creates Parser instances.
 */
class ParserModule {
    fun defaultParsers(
        timezone: TimeZone
    ): Array<PacketTypeParser> {
        return arrayOf(
            WeatherParser(timezone = timezone),
            PositionlessWeatherParser(),
            ObjectParser(timezone = timezone),
            ItemParser(),
            PositionParser(timezone = timezone),
            MessageParser()
        )
    }

    fun parser(
        infoParsers: Array<PacketTypeParser>,
        logger: KimchiLogger = EmptyLogger
    ): AprsParser = KarpsParser(infoParsers, logger = logger)

    /**
     * Create a standard packet parser with the default parsing modules.
     */
    fun defaultParser(
        logger: KimchiLogger = EmptyLogger,
        timezone: TimeZone = TimeZone.currentSystemDefault()
    ): AprsParser = parser(defaultParsers(timezone), logger)
}
