package com.inkapplications.ack.parser.status

import com.inkapplications.ack.parser.TestData
import com.inkapplications.ack.parser.timestamp.withUtcValues
import com.inkapplications.ack.structures.PacketData
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatusReportTransformerTest {
    private val transformer = StatusReportTransformer(TestData.timestampModule)

    @Test
    fun parse() {
        val given = ">Net Control Center"

        val result = transformer.parse(given)

        assertNull(result.timestamp)
        assertEquals("Net Control Center", result.status)
    }

    @Test
    fun withTime() {
        val given = ">092345zNet Control Center"

        val result = transformer.parse(given)

        assertEquals(9, result.timestamp?.toLocalDateTime(TimeZone.UTC)?.dayOfMonth)
        assertEquals(23, result.timestamp?.toLocalDateTime(TimeZone.UTC)?.hour)
        assertEquals(45, result.timestamp?.toLocalDateTime(TimeZone.UTC)?.minute)
        assertEquals("Net Control Center", result.status)
    }

    @Test
    fun generate() {
        val given = PacketData.StatusReport(
            timestamp = null,
            status = "Hello World"
        )

        val result = transformer.generate(given)

        assertEquals(">Hello World", result)
    }

    @Test
    fun generateTimestamp() {
        val given = PacketData.StatusReport(
            timestamp = TestData.now.withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
            ),
            status = "Hello World"
        )

        val result = transformer.generate(given)

        assertEquals(">092345zHello World", result)
    }
}
