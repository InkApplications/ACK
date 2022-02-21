package com.inkapplications.ack.codec.format

import kotlin.test.Test
import kotlin.test.assertEquals

class FloatsTest {
    @Test
    fun exact() {
        val result = 12.34f.fixedLength(
            decimals = 2,
            leftDigits = 2,
        )

        assertEquals("12.34", result)
    }

    @Test
    fun noLeft() {
        val result = 12.34f.fixedLength(2)

        assertEquals("12.34", result)
    }

    @Test
    fun short() {
        val result = 12.34f.fixedLength(3)

        assertEquals("12.340", result)
    }

    @Test
    fun long() {
        val result = 12.34f.fixedLength(1)

        assertEquals("12.3", result)
    }

    @Test
    fun longRounded() {
        val result = 12.39f.fixedLength(1)

        assertEquals("12.4", result)
    }

    @Test
    fun longNoRound() {
        val result = 12.39f.fixedLength(1, round = false)

        assertEquals("12.3", result)
    }
}
