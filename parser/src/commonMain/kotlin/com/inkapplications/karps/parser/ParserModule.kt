package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.position.CompositePositionParser
import com.inkapplications.karps.parser.position.CompressedPositionParser
import com.inkapplications.karps.parser.position.PlainPositionParser
import com.inkapplications.karps.parser.timestamp.*
import com.inkapplications.karps.parser.weather.WeatherParser
import com.soywiz.klock.TimezoneOffset
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger

/**
 * Creates Parser instances.
 */
class ParserModule {
    fun defaultTimestampParsers(
        timezoneOffsetMilliseconds: Double? = null
    ): Array<PacketInformationParser> = arrayOf(
        DhmzParser(),
        HmsParser(),
        MdhmParser(),
        if (timezoneOffsetMilliseconds != null)
            DhmlParser(TimezoneOffset(timezoneOffsetMilliseconds))
        else
            DhmlParser()
    )

    fun defaultParsers(
        timezoneOffsetMilliseconds: Double? = null
    ): Array<PacketInformationParser> = arrayOf(
        *defaultTimestampParsers(timezoneOffsetMilliseconds),
        CompositePositionParser(
            arrayOf(
                PlainPositionParser(),
                CompressedPositionParser()
            )
        ),
        DataExtensionParser(),
        WeatherParser()
    )

    fun parser(
        infoParsers: Array<PacketInformationParser>,
        logger: KimchiLogger = EmptyLogger
    ): AprsParser = KarpsParser(infoParsers, logger = logger)

    /**
     * Create a standard packet parser with the default parsing modules.
     */
    fun defaultParser(
        logger: KimchiLogger = EmptyLogger
    ): AprsParser = parser(defaultParsers(), logger)
}
