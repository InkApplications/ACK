package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DhmzParserTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = 9,
                hours = 22,
                minutes = 45,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        val result = DhmzParser().parse("092245z")

        assertEquals(expected, result)
    }
}
