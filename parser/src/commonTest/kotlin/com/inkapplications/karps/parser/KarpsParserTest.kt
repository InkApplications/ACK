package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KarpsParserTest {
    @Test
    fun ax25Parse() {
        val data = ubyteArrayOf(
            0x9cu, 0x94u, 0x6eu, 0xa0u,
            0x40u, 0x40u, 0xe0u, 0x9cu,
            0x6eu, 0x98u, 0x8au, 0x9au,
            0x40u, 0x60u, 0x9cu, 0x6eu,
            0x9eu, 0x9eu, 0x40u, 0x40u,
            0xe3u, 0x3eu, 0xf0u, 0x3du,
            0x46u, 0x6fu, 0x6fu
        ).toByteArray()

        val result = KarpsParser(emptyArray()).fromAx25(data)

        assertEquals(Address("NJ7P", ssid = "0"), result.destination)
        assertEquals(Address("N7LEM", ssid = "0"), result.source)
        assertEquals(1, result.digipeaters.size)
        assertEquals(Address("N7OO", ssid = "1"), result.digipeaters.first().address)
        assertEquals('=', result.dataTypeIdentifier)
        assertTrue(result is AprsPacket.Unknown)
        assertEquals("Foo", result.body)
    }

    @Test
    fun stringParse() {
        val data = "ON0CPS-S>APDG01,TCPIP*,qAC,ON0CPS-GS:;Foo"

        val result = KarpsParser(emptyArray()).fromString(data)

        assertEquals(Address("ON0CPS", "S"), result.source)
        assertEquals(Address("APDG01"), result.destination)
        assertEquals(listOf(
            Digipeater(Address("TCPIP"), heard = true),
            Digipeater(Address("qAC")),
            Digipeater(Address("ON0CPS", "GS"))
        ), result.digipeaters)
        assertEquals(';', result.dataTypeIdentifier)
        assertTrue(result is AprsPacket.Unknown)
        assertEquals("Foo", result.body)
    }
}
