package com.inkapplications.karps.parser.timestamp

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DhmzCodecTest {
    @Test
    fun decode() {
        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 22,
                minute = 45,
                second = 0,
                nanosecond = 0
            )
        val given = "092245z"

        val result = DhmzCodec().decode(given)

        assertEquals(expected, result)
    }

    @Test
    fun invalid() {
        val given = "092245/"

        assertFails { DhmzCodec().decode(given) }
    }

    @Test
    fun encode() {
        val given = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 22,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = DhmzCodec().encode(given)

        assertEquals("092245z", result)
    }
}
