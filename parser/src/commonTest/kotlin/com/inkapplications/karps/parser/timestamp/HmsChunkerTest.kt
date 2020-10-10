package com.inkapplications.karps.parser.timestamp

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class HmsChunkerTest {
    @Test
    fun parse() {
        val expected = Clock.System.now()
            .withUtcValues(
                hour = 23,
                minute = 45,
                second = 17,
                nanosecond = 0
            )

        val given = "234517hTest"

        val result = HmsChunker().popChunk(given)

        assertEquals(expected, result.result)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalid() {
        val given = "092245zTest"

        assertFails { HmsChunker().popChunk(given) }
    }
}
