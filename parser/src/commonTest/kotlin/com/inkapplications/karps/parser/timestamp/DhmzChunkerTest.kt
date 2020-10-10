package com.inkapplications.karps.parser.timestamp

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DhmzChunkerTest {
    @Test
    fun valid() {
        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 22,
                minute = 45,
                second = 0,
                nanosecond = 0
            )
        val given = "092245zTest"

        val result = DhmzChunker().popChunk(given)

        assertEquals(expected, result.result)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalid() {
        val given = "092245/Test"

        assertFails { DhmzChunker().popChunk(given) }
    }
}
