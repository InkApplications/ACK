package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val result = HmsParser().parse(PacketInformation('/', "234517h"))

        assertEquals(expected, result.timestamp)
    }
}
