package com.inkapplications.karps.parser.timestamp

import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MdhmChunkerTest {
    @Test
    fun parse() {
        val expected = Clock.System.now()
            .withUtcValues(
                month = Month.OCTOBER,
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        val given = "10092345Test"

        val result = MdhmChunker().popChunk(given)

        assertEquals(expected, result.result)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalid() {
        val given = "092245zTest"

        assertFails { MdhmChunker().popChunk(given) }
    }
}
