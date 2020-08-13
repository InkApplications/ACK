package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MdhmParserTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                month = Month.October,
                dayOfMonth = 9,
                hours = 23,
                minutes = 45,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
        val packet = TestData.Position.expected.copy(
            body = "10092345",
            timestamp = null
        )

        val result = MdhmParser().parse(packet)

        assertTrue(result is AprsPacket.Position, "Packet type should not change.")
        assertEquals(expected, result.timestamp)
    }
}
