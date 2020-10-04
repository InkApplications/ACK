package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset

/**
 * Parse Days/Hours/Minutes for local times.
 */
class DhmlChunker(
    private val timezone: TimezoneOffset
): Chunker<Timestamp> {
    override fun popChunk(data: String): Chunk<out Timestamp> {
        data[6].requireControl('/')
        val days = data.substring(0, 2).toInt()
        val hours = data.substring(2, 4).toInt()
        val minutes = data.substring(4, 6).toInt()
        val timestamp = DateTime.now()
            .copyDayOfMonth(
                dayOfMonth = days,
                hours = hours,
                minutes = minutes,
                seconds = 0,
                milliseconds = 0
            )
            .toOffsetUnadjusted(timezone)
            .utc
            .unixMillisLong
            .asTimestamp

        return Chunk(timestamp, data.substring(7))
    }
}
