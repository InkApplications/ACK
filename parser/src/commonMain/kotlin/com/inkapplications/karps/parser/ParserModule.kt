package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.item.ItemParser
import com.inkapplications.karps.parser.item.ObjectParser
import com.inkapplications.karps.parser.message.MessageParser
import com.inkapplications.karps.parser.position.PositionParser
import com.inkapplications.karps.parser.weather.PositionlessWeatherParser
import com.inkapplications.karps.parser.weather.WeatherParser
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger

/**
 * Creates Parser instances.
 */
class ParserModule {
    fun defaultParsers(
        timezoneOffsetMilliseconds: Double? = null
    ): Array<PacketTypeParser> {
        val timezone = timezoneOffsetMilliseconds?.let(::TimezoneOffset) ?: TimezoneOffset.local(DateTime.now())
        return arrayOf(
            WeatherParser(timezone),
            PositionlessWeatherParser(),
            ObjectParser(timezone),
            ItemParser(),
            PositionParser(timezone),
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
        logger: KimchiLogger = EmptyLogger
    ): AprsParser = parser(defaultParsers(), logger)
}
