package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.degreesFahrenheit
import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherParserTest {
    @Test
    fun parse() {
        val parser = PositionlessWeatherParser()

        val result = parser.parse(AprsPacket.Unknown(
            received = DateTime.now(),
            digipeaters = emptyList(),
            destination = Address("KE0YOG", ""),
            source = Address("KE0YOG", ""),
            dataTypeIdentifier = '_',
            body = "10090556c220s004g005t077r000p000P000h50b09900wRSW"
        ))

        assertTrue(result is AprsPacket.Weather)
        assertEquals(77.degreesFahrenheit, result.temperature)
    }

    @Test
    fun fail() {
        val parser = PositionlessWeatherParser()

        val result = runCatching {
            parser.parse(AprsPacket.Unknown(
                received = DateTime.now(),
                digipeaters = emptyList(),
                destination = Address("KE0YOG", ""),
                source = Address("KE0YOG", ""),
                dataTypeIdentifier = '_',
                body = "10090556c220s004g005t077r000p000P000h50b09900wRSW Unexpected comment"
            ))
        }


        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PacketFormatException)
    }
}
