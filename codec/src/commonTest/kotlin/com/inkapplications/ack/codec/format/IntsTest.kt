package com.inkapplications.ack.codec.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

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
    fun leftPadNegativeExact() {
        val result = (-7).leftPad(2)

        assertEquals("-7", result)
    }

    @Test
    fun leftPadNegativeShort() {
        val result = (-7).leftPad(3)

        assertEquals("-07", result)
    }

    @Test
    fun leftPadNegativeLong() {
        val result = (-7).leftPad(1)

        assertEquals("-7", result)
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

    @Test
    fun fixedLengthNegativeExact() {
        val result = (-7).fixedLength(2)

        assertEquals("-7", result)
    }

    @Test
    fun fixedLengthNegativeShort() {
        val result = (-7).fixedLength(3)

        assertEquals("-07", result)
    }

    @Test
    fun fixedLengthNegativeLong() {
        val result = (-69).fixedLength(2)

        assertEquals("-9", result)
    }

    @Test
    fun tooShort() {
        assertFails { (-6).fixedLength(1) }
    }
}
