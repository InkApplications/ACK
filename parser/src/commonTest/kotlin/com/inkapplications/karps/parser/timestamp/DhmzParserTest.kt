package com.inkapplications.karps.parser.timestamp

import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DhmzParserTest {
    @Test
    fun parse() {
        val now = DateTime.now()
        val result = DhmzParser().parse("092245z")

        assertEquals(now.year, result.year)
        assertEquals(now.month, result.month)
        assertEquals(9, result.dayOfMonth)
        assertEquals(22, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(0, result.seconds)
    }
}
