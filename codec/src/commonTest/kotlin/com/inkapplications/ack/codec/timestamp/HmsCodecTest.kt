package com.inkapplications.ack.codec.timestamp

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class HmsCodecTest {
    @Test
    fun decode() {
        val expected = Clock.System.now()
            .withUtcValues(
                hour = 23,
                minute = 45,
                second = 17,
                nanosecond = 0
            )

        val given = "234517h"

        val result = HmsCodec().decode(given)

        assertEquals(expected, result)
    }

    @Test
    fun invalid() {
        val given = "092245z"

        assertFails { HmsCodec().decode(given) }
    }

    @Test
    fun encode() {
        val given = Clock.System.now()
            .withUtcValues(
                hour = 23,
                minute = 45,
                second = 17,
                nanosecond = 0
            )

        val result = HmsCodec().encode(given)

        assertEquals("234517h", result)
    }
}
