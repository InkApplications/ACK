package com.inkapplications.karps.parser.timestamp

import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class HmsParserTest {
    @Test
    fun parse() {
        val now = DateTime.now()
        val result = HmsParser().parse("234517h")

        assertEquals(now.year, result.year)
        assertEquals(now.month, result.month)
        assertEquals(now.dayOfMonth, result.dayOfMonth)
        assertEquals(23, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(17, result.seconds)
    }
}
