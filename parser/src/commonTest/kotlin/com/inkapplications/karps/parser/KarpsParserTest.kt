package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Digipeater
import com.inkapplications.karps.structures.PacketRoute
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

        val result = KarpsParser(emptyArray(), emptyArray()).fromAx25(data)

        assertEquals(Address("NJ7P", ssid = "0"), result.route.destination)
        assertEquals(Address("N7LEM", ssid = "0"), result.route.source)
        assertEquals(1, result.route.digipeaters.size)
        assertEquals(Address("N7OO", ssid = "1"), result.route.digipeaters.first().address)
        assertTrue(result is AprsPacket.Unknown)
        assertEquals("=Foo", result.body)
    }

    @Test
    fun stringParse() {
        val data = "ON0CPS-S>APDG01,TCPIP*,qAC,ON0CPS-GS:;Foo"

        val result = KarpsParser(emptyArray(), emptyArray()).fromString(data)

        assertEquals(Address("ON0CPS", "S"), result.route.source)
        assertEquals(Address("APDG01"), result.route.destination)
        assertEquals(listOf(
            Digipeater(Address("TCPIP"), heard = true),
            Digipeater(Address("qAC")),
            Digipeater(Address("ON0CPS", "GS"))
        ), result.route.digipeaters)
        assertTrue(result is AprsPacket.Unknown)
        assertEquals(";Foo", result.body)
    }

    @Test
    fun stringEncode() {
        val data = AprsPacket.Unknown(
            route = PacketRoute(
                source = Address("T3ST", "S"),
                destination = Address("T3ST", "D"),
                digipeaters = listOf(
                    Digipeater(
                        address = Address("T3ST", "A"),
                        heard = true
                    ),
                    Digipeater(
                        address = Address("T3ST", "B"),
                    )
                ),
            ),
            body = "=Hello World"
        )

        val result = KarpsParser(emptyArray(), arrayOf(UnknownPacketGenertator)).toString(data)

        assertEquals("T3ST-S>T3ST-D,T3ST-A*,T3ST-B:=Hello World", result)
    }
}

