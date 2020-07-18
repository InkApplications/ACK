package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Cardinal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AprsParserTest {
    @Test
    fun parsePosition() {
        val parser = ParserModule().parser()

        val result = parser.fromString("KV4JW>APDR15,TCPIP*,qAC,T2HUN:=3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/")

        assertEquals("KV4JW", result.source.callsign)
        assertEquals("0", result.source.ssid)

        assertEquals("APDR15", result.destination.callsign)
        assertEquals("0", result.source.ssid)

        assertEquals(listOf("TCPIP", "qAC", "T2HUN"), result.digipeaters.map { it.address.callsign })
        assertEquals(listOf("0", "0", "0"), result.digipeaters.map { it.address.ssid })
        assertEquals(listOf(true, false, false), result.digipeaters.map { it.heard })

        assertEquals('=', result.dataTypeIdentifier)
        assertTrue(result is AprsPacket.Position, "Packet is parsed as a position")

        assertEquals(37.778667, result.coordinates.latitude.decimal, 0.000001)
        assertEquals(-84.0365, result.coordinates.longitude.decimal, 0.000001)
    }

    @Test
    fun parseCompressedPosition() {
        val parser = ParserModule().parser()

        val result = parser.fromString("REDKNL>APOT30,KE7JVX-10*,WIDE2-1,qAR,K7YI-4:!S;an%2#Co# st130F N7YSE Red Knoll")

        assertEquals("REDKNL", result.source.callsign)
        assertEquals("0", result.source.ssid)

        assertEquals("APOT30", result.destination.callsign)
        assertEquals("0", result.source.ssid)

        assertEquals(listOf("KE7JVX", "WIDE2", "qAR", "K7YI"), result.digipeaters.map { it.address.callsign })
        assertEquals(listOf("10", "1", "0", "4"), result.digipeaters.map { it.address.ssid })
        assertEquals(listOf(true, false, false, false), result.digipeaters.map { it.heard })

        assertEquals('!', result.dataTypeIdentifier)
        assertTrue(result is AprsPacket.Position, "Packet is parsed as a position")

        assertEquals(37, result.coordinates.latitude.degrees)
        assertEquals(9, result.coordinates.latitude.minutes)
        assertEquals(19.83, result.coordinates.latitude.seconds, .01)
        assertEquals(Cardinal.North, result.coordinates.latitude.cardinal)

        assertEquals(112, result.coordinates.longitude.degrees)
        assertEquals(38, result.coordinates.longitude.minutes)
        assertEquals(7.87, result.coordinates.longitude.seconds, .01)
        assertEquals(Cardinal.West, result.coordinates.longitude.cardinal)
    }
}
