package com.inkapplications.karps.parser.status

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.timestamp.withUtcValues
import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatusReportTransformerTest {
    private val transformer = StatusReportTransformer(TestData.timestampModule)

    @Test
    fun parse() {
        val given = "Net Control Center"

        val result = transformer.parse(TestData.prototype.copy(body = given))

        assertNull(result.timestamp)
        assertEquals("Net Control Center", result.status)
    }

    @Test
    fun withTime() {
        val given = "092345zNet Control Center"

        val result = transformer.parse(TestData.prototype.copy(body = given))

        assertEquals(9, result.timestamp?.toLocalDateTime(TimeZone.UTC)?.dayOfMonth)
        assertEquals(23, result.timestamp?.toLocalDateTime(TimeZone.UTC)?.hour)
        assertEquals(45, result.timestamp?.toLocalDateTime(TimeZone.UTC)?.minute)
        assertEquals("Net Control Center", result.status)
    }

    @Test
    fun generate() {
        val given = AprsPacket.StatusReport(
            dataTypeIdentifier = ':',
            source = Address("KE0YOG"),
            destination = Address("KE0YOG"),
            digipeaters = listOf(),
            timestamp = null,
            status = "Hello World"
        )

        val result = transformer.generate(given)

        assertEquals("Hello World", result)
    }

    @Test
    fun generateTimestamp() {
        val given = AprsPacket.StatusReport(
            dataTypeIdentifier = ':',
            source = Address("KE0YOG"),
            destination = Address("KE0YOG"),
            digipeaters = listOf(),
            timestamp = TestData.now.withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
            ),
            status = "Hello World"
        )

        val result = transformer.generate(given)

        assertEquals("092345zHello World", result)
    }
}
