package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MdhmChunkerTest {
    @Test
    fun parse() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                month = Month.October,
                dayOfMonth = 9,
                hours = 23,
                minutes = 45,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

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
