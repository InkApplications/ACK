package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DhmzChunkerTest {
    @Test
    fun valid() {
        val expected = DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = 9,
                hours = 22,
                minutes = 45,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp
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
