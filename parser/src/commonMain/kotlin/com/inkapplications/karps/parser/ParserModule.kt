package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.position.CompressedPositionParser
import com.inkapplications.karps.parser.position.PlainPositionParser
import com.inkapplications.karps.parser.timestamp.*

class ParserModule {
    fun parser(): AprsParser {
        val timestampParser = CompositeTimestampParser(
            DhmzParser(),
            HmsParser(),
            MdhmParser(),
            DhmlParser()
        )
        return AprsParser(
            parsers = listOf(
                PlainPositionParser(timestampParser),
                CompressedPositionParser(timestampParser)
            )
        )
    }
}
