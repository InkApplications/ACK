package com.inkapplications.karps.parser.message

import com.inkapplications.karps.parser.TestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class MessageParserTest {
    @Test
    fun parseMessage() {
        val given = "WU2Z     :Testing"

        val result = MessageParser().parse(TestData.prototype.copy(body = given))

        assertEquals("WU2Z", result.addressee.callsign)
        assertEquals("0", result.addressee.ssid)
        assertEquals("Testing", result.message)
        assertNull(result.messageNumber)
    }

    @Test
    fun parseMessageWithNumber() {
        val given = "WU2Z-2   :Testing{003"

        val result = MessageParser().parse(TestData.prototype.copy(body = given))

        assertEquals("WU2Z", result.addressee.callsign)
        assertEquals("2", result.addressee.ssid)
        assertEquals("Testing", result.message)
        assertEquals(3, result.messageNumber)
    }

    @Test
    fun parseNonMessage() {
        val given = "LEA_092345z4903.50N/07201.75W>088/036"

        assertFails { MessageParser().parse(TestData.prototype.copy(body = given)) }
    }
}
