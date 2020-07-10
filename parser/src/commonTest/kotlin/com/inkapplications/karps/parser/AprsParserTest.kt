package com.inkapplications.karps.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class AprsParserTest {
    @Test
    fun parse() {
        val parser = AprsParser()

        val result = parser.fromString("KV4JW>APDR15,TCPIP*,qAC,T2HUN:=3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/")

        assertEquals("KV4JW", result.source.callsign)
        assertEquals(0, result.source.ssid)

        assertEquals("APDR15", result.destination.callsign)
        assertEquals(0, result.source.ssid)

        assertEquals(listOf("APDR15", "TCPIP*", "qAC", "T2HUN"), result.digipeaters.map { it.callsign })
        assertEquals(listOf(0, 0, 0, 0).map { it.toByte() }, result.digipeaters.map { it.ssid })
    }
}
