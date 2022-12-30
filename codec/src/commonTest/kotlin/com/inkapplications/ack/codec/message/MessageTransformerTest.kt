package com.inkapplications.ack.codec.message

import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.StationAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class MessageTransformerTest {
    @Test
    fun parseMessage() {
        val given = ":WU2Z     :Testing"

        val result = MessageTransformer().parse(given)

        assertEquals("WU2Z", result.addressee.callsign.canonical)
        assertEquals("0", result.addressee.ssid.value)
        assertEquals("Testing", result.message)
        assertNull(result.messageNumber)
    }

    @Test
    fun parseMessageWithNumber() {
        val given = ":WU2Z-2   :Testing{003"

        val result = MessageTransformer().parse(given)

        assertEquals("WU2Z", result.addressee.callsign.canonical)
        assertEquals("2", result.addressee.ssid.value)
        assertEquals("Testing", result.message)
        assertEquals(3, result.messageNumber)
    }

    @Test
    fun parseNonMessage() {
        val given = ":LEA_092345z4903.50N/07201.75W>088/036"

        assertFails { MessageTransformer().parse(given) }
    }

    @Test
    fun generate() {
        val given = PacketData.Message(
            addressee = StationAddress("KE0YOG", "3"),
            message = "Hello World",
            messageNumber = 3,
        )

        val result = MessageTransformer().generate(given)

        assertEquals(":KE0YOG-3 :Hello World{003", result)
    }

    @Test
    fun generateNoNumber() {
        val given = PacketData.Message(
            addressee = StationAddress("KE0YOG", "3"),
            message = "Hello World",
            messageNumber = null,
        )

        val result = MessageTransformer().generate(given)

        assertEquals(":KE0YOG-3 :Hello World", result)
    }
}
