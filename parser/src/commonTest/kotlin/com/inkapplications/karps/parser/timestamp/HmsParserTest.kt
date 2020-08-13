package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HmsParserTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                hours = 23,
                minutes = 45,
                seconds = 17,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
        val packet = TestData.Position.expected.copy(
            body = "234517h",
            timestamp = null
        )

        val result = HmsParser().parse(packet)

        assertTrue(result is AprsPacket.Position, "Packet type should not change.")
        assertEquals(expected, result.timestamp)
    }
}
