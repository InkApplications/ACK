package com.inkapplications.ack.codec.altitude

import inkapplications.spondee.measure.us.feet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AltitudeChunkerTest {
    @Test
    fun startOfMessage() {
        val given = "/A=123456Hello World"

        val result = AltitudeChunker.popChunk(given)

        assertEquals(123456.feet, result.result, "Altitude is parsed from body.")
        assertEquals("Hello World", result.remainingData, "Parsed data is removed from body.")
    }

    @Test
    fun middleOfMessage() {
        val given = "Hello I'm at/A=001234!"

        val result = AltitudeChunker.popChunk(given)

        assertEquals(1234.feet, result.result, "Altitude is parsed from body.")
        assertEquals("Hello I'm at!", result.remainingData, "Parsed data is removed from body.")
    }

    @Test
    fun notPresent() {
        val given = "Hello Wald!"

        assertFails { AltitudeChunker.popChunk(given) }
    }
}
