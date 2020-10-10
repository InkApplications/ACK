package com.inkapplications.karps.parser.timestamp

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DhmlChunkerTest {
    @Test
    fun parse() {
        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 21,
                minute = 45,
                second = 0,
                nanosecond = 0
            )
        val given = "092245/Test"

        val result = DhmlChunker(timezone = TimeZone.of("+1")).popChunk(given)

        assertEquals(expected, result.result)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalid() {
        val given = "092245zTest"

        assertFails { DhmlChunker(timezone = TimeZone.of("+1")).popChunk(given) }
    }
}
