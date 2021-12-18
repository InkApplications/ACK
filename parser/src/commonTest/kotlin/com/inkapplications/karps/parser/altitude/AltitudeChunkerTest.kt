package com.inkapplications.karps.parser.altitude

import inkapplications.spondee.measure.Feet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AltitudeChunkerTest {
    @Test
    fun startOfMessage() {
        val given = "/A=123456Hello World"

        val result = AltitudeChunker.popChunk(given)

        assertEquals(Feet.of(123456), result.result, "Altitude is parsed from body.")
        assertEquals("Hello World", result.remainingData, "Parsed data is removed from body.")
    }

    @Test
    fun middleOfMessage() {
        val given = "Hello I'm at/A=001234!"

        val result = AltitudeChunker.popChunk(given)

        assertEquals(Feet.of(1234), result.result, "Altitude is parsed from body.")
        assertEquals("Hello I'm at!", result.remainingData, "Parsed data is removed from body.")
    }

    @Test
    fun notPresent() {
        val given = "Hello Wald!"

        assertFails { AltitudeChunker.popChunk(given) }
    }
}
