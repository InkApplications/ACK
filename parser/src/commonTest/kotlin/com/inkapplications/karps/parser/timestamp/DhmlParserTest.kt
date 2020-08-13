package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.*
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DhmlParserTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = 9,
                hours = 21,
                minutes = 45,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
        val packet = TestData.Position.expected.copy(
            body = "092245/",
            timestamp = null
        )
        val result = DhmlParser(TimezoneOffset(60.minutes)).parse(packet)

        assertTrue(result is AprsPacket.Position, "Packet type should not change.")
        assertEquals(expected, result.timestamp)
    }
}
