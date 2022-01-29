package com.inkapplications.ack.parser.format

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {
    @Test
    fun rightPadExact() {
        val result = "hi".rightPad(2)

        assertEquals("hi", result)
    }

    @Test
    fun rightPadShort() {
        val result = "hi".rightPad(4)

        assertEquals("hi  ", result)
    }

    @Test
    fun leftPadLong() {
        val result = "hi".rightPad(1)

        assertEquals("hi", result)
    }

    @Test
    fun fixedLengthExact() {
        val result = "hi".fixedLength(2)

        assertEquals("hi", result)
    }

    @Test
    fun fixedLengthShort() {
        val result = "hi".fixedLength(3)

        assertEquals("hi ", result)
    }

    @Test
    fun fixedLengthLong() {
        val result = "hello".fixedLength(2)

        assertEquals("he", result)
    }
}
