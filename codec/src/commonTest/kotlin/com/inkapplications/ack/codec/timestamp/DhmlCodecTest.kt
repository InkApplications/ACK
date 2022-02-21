package com.inkapplications.ack.codec.timestamp

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DhmlCodecTest {
    @Test
    fun decode() {
        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 21,
                minute = 45,
                second = 0,
                nanosecond = 0
            )
        val given = "092245/"

        val result = DhmlCodec(timezone = TimeZone.of("+1")).decode(given)

        assertEquals(expected, result)
    }

    @Test
    fun invalid() {
        val given = "092245z"

        assertFails { DhmlCodec(timezone = TimeZone.of("+1")).decode(given) }
    }

    @Test
    fun encode() {
        val given = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 21,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val result = DhmlCodec(timezone = TimeZone.of("+1")).encode(given)

        assertEquals("092245/", result)
    }
}
