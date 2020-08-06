package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.timestamp.CompositeTimestampParser
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private val module = ParserModule()
    private val parser = module.defaultTimestampParsers()
        .let { CompositeTimestampParser(*it) }
        .let { module.defaultParsers(it) }
        .let { CompositeInformationParser(*it) }
        .let { KarpsParser(it, FixedClock(TestData.now)) }

    @Test
    fun parseAll() {
        TestData.all.forEach { test ->
            val result = parser.fromString(test.packet)
            assertEquals(test.expected, result, "Test failed to parse data ${TestData::class.simpleName}::${test::class.simpleName}")
        }
    }
}
