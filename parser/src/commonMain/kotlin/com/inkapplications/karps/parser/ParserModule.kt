package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.position.CompressedPositionParser
import com.inkapplications.karps.parser.position.PlainPositionParser
import com.inkapplications.karps.parser.timestamp.*

/**
 * Creates Parser instances.
 */
class ParserModule {
    fun defaultTimestampParsers(): Array<TimestampParser> = arrayOf(
        DhmzParser(),
        HmsParser(),
        MdhmParser(),
        DhmlParser()
    )

    fun defaultParsers(timestampParser: TimestampParser): Array<PacketInformationParser> = arrayOf(
        PlainPositionParser(timestampParser),
        CompressedPositionParser(timestampParser)
    )

    fun parser(
        infoParser: PacketInformationParser
    ): AprsParser = KarpsParser(infoParser)

    /**
     * Create a standard packet parser with the default parsing modules.
     */
    fun defaultParser(): AprsParser = defaultTimestampParsers()
        .let { CompositeTimestampParser(*it) }
        .let { defaultParsers(it) }
        .let { CompositeInformationParser(*it) }
        .let { parser(it) }
}