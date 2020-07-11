package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AprsParserTest {
    @Test
    fun parse() {
        val parser = AprsParser()

        val result = parser.fromString("KV4JW>APDR15,TCPIP*,qAC,T2HUN:=3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/")

        assertEquals("KV4JW", result.source.callsign)
        assertEquals(0, result.source.ssid)

        assertEquals("APDR15", result.destination.callsign)
        assertEquals(0, result.source.ssid)

        assertEquals(listOf("TCPIP", "qAC", "T2HUN"), result.digipeaters.map { it.address.callsign })
        assertEquals(listOf(0, 0, 0).map { it.toByte() }, result.digipeaters.map { it.address.ssid })
        assertEquals(listOf(true, false, false), result.digipeaters.map { it.repeated })

        assertEquals('=', result.dataTypeIdentifier)
        assertTrue(result is AprsPacket.Position, "Packet is parsed as a position")

        assertEquals(37.778667, result.coordinates.latitude.decimal, 0.000001)
        assertEquals(-84.0365, result.coordinates.longitude.decimal, 0.000001)
    }
}
