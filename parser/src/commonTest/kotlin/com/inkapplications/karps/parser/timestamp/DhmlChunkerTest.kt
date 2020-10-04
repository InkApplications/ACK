package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.structures.unit.*
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DhmlChunkerTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = 9,
                hours = 21,
                minutes = 45,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
        val given = "092245/Test"

        val result = DhmlChunker(TimezoneOffset(60.minutes)).popChunk(given)

        assertEquals(expected, result.parsed)
        assertEquals("Test", result.remainingData)
    }

    @Test
    fun invalid() {
        val given = "092245zTest"

        assertFails { DhmlChunker(TimezoneOffset(60.minutes)).popChunk(given) }
    }
}
