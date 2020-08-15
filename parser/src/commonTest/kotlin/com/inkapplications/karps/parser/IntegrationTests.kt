package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.TestData.now
import com.inkapplications.karps.structures.Address
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private val module = ParserModule()
    private val parser = module.defaultParsers(0.0)
        .let { KarpsParser(it, clock = FixedClock(now)) }


    @Test
    fun parseAll() {
        TestData.all.forEach { test ->
            val result = parser.fromString(test.packet)
            assertEquals(test.expected, result, "Test failed to parse data ${TestData::class.simpleName}::${test::class.simpleName}")
        }
    }

    @Test
    fun parseAx25() {
        val data = ubyteArrayOf(
            0x9cu, 0x94u, 0x6eu, 0xa0u,
            0x40u, 0x40u, 0xe0u, 0x9cu,
            0x6eu, 0x98u, 0x8au, 0x9au,
            0x40u, 0x60u, 0x9cu, 0x6eu,
            0x9eu, 0x9eu, 0x40u, 0x40u,
            0xe3u, 0x3eu, 0xf0u, 0x00u,
            0x00u
        ).toByteArray()
        val result = parser.fromAx25(data)

        assertEquals(Address("NJ7P", ssid = "0"), result.destination)
        assertEquals(Address("N7LEM", ssid = "0"), result.source)
        assertEquals(1, result.digipeaters.size)
        assertEquals(Address("N7OO", ssid = "1"), result.digipeaters.first().address)
    }
}
