package com.inkapplications.karps.parser.timestamp

import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class MdhmParserTest {
    @Test
    fun parse() {
        val now = DateTime.now()
        val result = MdhmParser().parse("10092345")

        assertEquals(now.year, result.year)
        assertEquals(Month.October, result.month)
        assertEquals(9, result.dayOfMonth)
        assertEquals(23, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(0, result.seconds)
    }
}
