package com.inkapplications.karps.parser

import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.minutes
import kotlin.test.Test
import kotlin.test.assertEquals

class TimestampsTest {
    val now = DateTime.now()

    @Test
    fun fromDhmUtc() {
        val result = Timestamps(TimezoneOffset(0.minutes)).parse("092245z")

        assertEquals(now.year, result.year)
        assertEquals(now.month, result.month)
        assertEquals(9, result.dayOfMonth)
        assertEquals(22, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(0, result.seconds)
    }

    @Test
    fun fromDhmLocal() {
        val result = Timestamps(TimezoneOffset(60.minutes)).parse("092245/")

        assertEquals(now.year, result.year)
        assertEquals(now.month, result.month)
        assertEquals(9, result.dayOfMonth)
        assertEquals(21, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(0, result.seconds)
    }

    @Test
    fun fromHmsUtc() {
        val result = Timestamps().parse("234517h")

        assertEquals(now.year, result.year)
        assertEquals(now.month, result.month)
        assertEquals(now.dayOfMonth, result.dayOfMonth)
        assertEquals(23, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(17, result.seconds)
    }

    @Test
    fun fromMdhm() {
        val result = Timestamps().parse("10092345")

        assertEquals(now.year, result.year)
        assertEquals(Month.October, result.month)
        assertEquals(9, result.dayOfMonth)
        assertEquals(23, result.hours)
        assertEquals(45, result.minutes)
        assertEquals(0, result.seconds)
    }
}
