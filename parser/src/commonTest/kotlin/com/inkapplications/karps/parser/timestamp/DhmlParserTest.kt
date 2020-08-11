package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.minutes
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val result = DhmlParser(TimezoneOffset(60.minutes)).parse(PacketInformation('/', "092245/"))

        assertEquals(expected, result.timestamp)
    }
}
