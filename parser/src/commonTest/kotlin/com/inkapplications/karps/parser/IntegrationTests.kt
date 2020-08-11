package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.TestData.now
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private val module = ParserModule()
    private val parser = module.defaultParsers(0.0)
        .let { KarpsParser(it, FixedClock(now)) }


    @Test
    fun parseAll() {
        TestData.all.forEach { test ->
            val result = parser.fromString(test.packet)
            assertEquals(test.expected, result, "Test failed to parse data ${TestData::class.simpleName}::${test::class.simpleName}")
        }
    }
}
