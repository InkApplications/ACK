package com.inkapplications.karps.parser.status

import com.inkapplications.karps.parser.TestData
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatusReportParserTest {
    @Test
    fun parse() {
        val given = "Net Control Center"

        val result = StatusReportParser().parse(TestData.prototype.copy(body = given))

        assertNull(result.time)
        assertEquals("Net Control Center", result.status)
    }

    @Test
    fun withTime() {
        val given = "092345zNet Control Center"

        val result = StatusReportParser().parse(TestData.prototype.copy(body = given))

        assertEquals(9, result.time?.toLocalDateTime(TimeZone.UTC)?.dayOfMonth)
        assertEquals(23, result.time?.toLocalDateTime(TimeZone.UTC)?.hour)
        assertEquals(45, result.time?.toLocalDateTime(TimeZone.UTC)?.minute)
        assertEquals("Net Control Center", result.status)
    }

}
