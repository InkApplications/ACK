package com.inkapplications.karps.parser.timestamp

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.minutes
import kotlin.test.Test
import kotlin.test.assertEquals

class DhmlParserTest {
    @Test
    fun parse() {
        val now = DateTime.now()
        val result = DhmlParser(TimezoneOffset(60.minutes)).parse("092245/")

        assertEquals(now.year, result.year)
        assertEquals(now.month, result.month)
        assertEquals(9, result.dayOfMonth)
        assertEquals(21, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(0, result.seconds)
    }
}
