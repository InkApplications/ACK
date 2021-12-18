package com.inkapplications.karps.parser.format

import kotlin.test.Test
import kotlin.test.assertEquals

class IntsTest {
    @Test
    fun leftPadExact() {
        val result = 5.leftPad(1)

        assertEquals("5", result)
    }

    @Test
    fun leftPadShort() {
        val result = 5.leftPad(3)

        assertEquals("005", result)
    }

    @Test
    fun leftPadLong() {
        val result = 123.leftPad(2)

        assertEquals("123", result)
    }

    @Test
    fun fixedLengthExact() {
        val result = 5.fixedLength(1)

        assertEquals("5", result)
    }

    @Test
    fun fixedLengthShort() {
        val result = 5.fixedLength(3)

        assertEquals("005", result)
    }

    @Test
    fun fixedLengthLong() {
        val result = 123.fixedLength(2)

        assertEquals("99", result)
    }
}
