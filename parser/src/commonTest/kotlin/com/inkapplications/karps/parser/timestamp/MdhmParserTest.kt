package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import kotlin.test.Test
import kotlin.test.assertEquals

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

        val result = MdhmParser().parse(PacketInformation('/', "10092345"))

        assertEquals(expected, result.timestamp)
    }
}
