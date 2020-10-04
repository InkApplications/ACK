package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class HmsChunkerTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                hours = 23,
                minutes = 45,
                seconds = 17,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        val given = "234517hTest"

        val result = HmsChunker().popChunk(given)

        assertEquals(expected, result.parsed)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalid() {
        val given = "092245zTest"

        assertFails { HmsChunker().popChunk(given) }
    }
}
