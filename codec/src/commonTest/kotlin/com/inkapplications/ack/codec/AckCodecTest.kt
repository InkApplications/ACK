package com.inkapplications.ack.codec

import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.station.StationAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AckCodecTest {
    @Test
    fun ax25Parse() {
        val data = ubyteArrayOf(
            0x9cu, 0x94u, 0x6eu, 0xa0u,
            0x40u, 0x40u, 0xe0u, 0x9cu,
            0x6eu, 0x98u, 0x8au, 0x9au,
            0x40u, 0x60u, 0x9cu, 0x6eu,
            0x9eu, 0x9eu, 0x40u, 0x40u,
            0xe3u, 0x03u, 0xf0u, 0x3du,
            0x46u, 0x6fu, 0x6fu
        ).toByteArray()

        val result = AckCodec(arrayOf(UnknownPacketTransformer), emptyArray()).fromAx25(data)

        assertEquals(StationAddress("NJ7P", ssid = "0"), result.route.destination)
        assertEquals(StationAddress("N7LEM", ssid = "0"), result.route.source)
        assertEquals(1, result.route.digipeaters.size)
        assertEquals(StationAddress("N7OO", ssid = "1"), result.route.digipeaters.first().address)
        val packetData = result.data
        assertTrue(packetData is PacketData.Unknown)
        assertEquals("=Foo", packetData.body)
    }

    @Test
    fun stringParse() {
        val data = "ON0CPS-S>APDG01,TCPIP*,qAC,ON0CPS-GS:;Foo"

        val result = AckCodec(arrayOf(UnknownPacketTransformer), emptyArray()).fromString(data)

        assertEquals(StationAddress("ON0CPS", "S"), result.route.source)
        assertEquals(StationAddress("APDG01"), result.route.destination)
        assertEquals(listOf(
            Digipeater(StationAddress("TCPIP"), heard = true),
            Digipeater(StationAddress("qAC")),
            Digipeater(StationAddress("ON0CPS", "GS"))
        ), result.route.digipeaters)
        val packetData = result.data
        assertTrue(packetData is PacketData.Unknown)
        assertEquals(";Foo", packetData.body)
    }

    @Test
    fun stringEncode() {
        val data = AprsPacket(
            route = PacketRoute(
                source = StationAddress("T3ST", "S"),
                destination = StationAddress("T3ST", "D"),
                digipeaters = listOf(
                    Digipeater(
                        address = StationAddress("T3ST", "A"),
                        heard = true
                    ),
                    Digipeater(
                        address = StationAddress("T3ST", "B"),
                    )
                ),
            ),
            data = PacketData.Unknown("=Hello World")
        )

        val result = AckCodec(emptyArray(), arrayOf(UnknownPacketTransformer)).toString(data)

        assertEquals("T3ST-S>T3ST-D,T3ST-A*,T3ST-B:=Hello World", result)
    }

    @Test
    fun byteEncode() {
        val data = AprsPacket(
            route = PacketRoute(
                source = StationAddress("N7LEM", "0"),
                destination = StationAddress("NJ7P", "0"),
                digipeaters = listOf(
                    Digipeater(
                        address = StationAddress("N7OO", ssid = "1"),
                        heard = true,
                    ),
                ),
            ),
            data = PacketData.Unknown("=Foo")
        )

        val result = AckCodec(emptyArray(), arrayOf(UnknownPacketTransformer)).toAx25(data)

        val expected = ubyteArrayOf(
            0x9cu, 0x94u, 0x6eu, 0xa0u,
            0x40u, 0x40u, 0xe0u, 0x9cu,
            0x6eu, 0x98u, 0x8au, 0x9au,
            0x40u, 0x60u, 0x9cu, 0x6eu,
            0x9eu, 0x9eu, 0x40u, 0x40u,
            0xe3u, 0x03u, 0xf0u, 0x3du,
            0x46u, 0x6fu, 0x6fu
        ).toByteArray()

        assertEquals(expected.decodeToString(), result.decodeToString())
    }
}

