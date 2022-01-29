package com.inkapplications.ack.parser.timestamp

import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MdhmCodecTest {
    @Test
    fun decode() {
        val expected = Clock.System.now()
            .withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val given = "10092345"

        val result = MdhmCodec().decode(given)

        assertEquals(expected, result)
    }

    @Test
    fun invalid() {
        val given = "092245z"

        assertFails { MdhmCodec().decode(given) }
    }

    @Test
    fun encode() {
        val given = Clock.System.now()
            .withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = MdhmCodec().encode(given)


        assertEquals("10092345", result)
    }
}
