package com.inkapplications.karps.parser.altitude

import com.inkapplications.karps.structures.unit.feet
import kotlin.test.*

class AltitudeChunkerTest {
    @Test
    fun startOfMessage() {
        val given = "/A=123456Hello World"

        val result = AltitudeChunker.popChunk(given)

        assertEquals(123456.feet, result.parsed, "Altitude is parsed from body.")
        assertEquals("Hello World", result.remainingData, "Parsed data is removed from body.")
    }

    @Test
    fun middleOfMessage() {
        val given = "Hello I'm at/A=001234!"

        val result = AltitudeChunker.popChunk(given)

        assertEquals(1234.feet, result.parsed, "Altitude is parsed from body.")
        assertEquals("Hello I'm at!", result.remainingData, "Parsed data is removed from body.")
    }

    @Test
    fun notPresent() {
        val given = "Hello Wald!"

        assertFails { AltitudeChunker.popChunk(given) }
    }
}
