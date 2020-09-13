package com.inkapplications.karps.parser.altitude

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.feet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AltitudeParserTest {
    @Test
    fun startOfMessage() {
        val given = TestData.Position.expected.copy(
            body = "/A=001234Hello World!"
        )

        val result = AltitudeCommentParser().parse(given)

        assertTrue(result is AprsPacket.Position, "Packet type should not change.")
        assertEquals(1234.feet, result.altitude, "Altitude is parsed from body.")
        assertEquals("Hello World!", result.body, "Parsed data is removed from body.")
    }

    @Test
    fun middleOfMessage() {
        val given = TestData.Position.expected.copy(
            body = "Hello I'm at/A=001234!"
        )

        val result = AltitudeCommentParser().parse(given)

        assertTrue(result is AprsPacket.Position, "Packet type should not change.")
        assertEquals(1234.feet, result.altitude, "Altitude is parsed from body.")
        assertEquals("Hello I'm at!", result.body, "Parsed data is removed from body.")
    }

    @Test
    fun notPresent() {
        val given = TestData.Position.expected.copy(
            body = "Hello Wald!"
        )

        val result = AltitudeCommentParser().parse(given)

        assertTrue(result is AprsPacket.Position, "Packet type should not change.")
        assertNull(result.altitude, "Altitude null if not found.")
        assertEquals("Hello Wald!", result.body, "Body should remain the same if no altitude is parsed.")
    }

    @Test
    fun unexpectedAltitude() {
        val given = TestData.prototype.copy(
            body = "/A=001234Hello World!"
        )

        val result = AltitudeCommentParser().parse(given)

        assertEquals(given, result, "Packet should be unmodified if type doesn't support altitude")
    }
}
