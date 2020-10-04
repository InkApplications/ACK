package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month

/**
 * Parse Month/Day/Hours/Minutes format.
 */
class MdhmChunker: Chunker<Timestamp> {
    override fun popChunk(data: String): Chunk<out Timestamp> {
        val month = data.substring(0, 2).toInt()
        val days = data.substring(2, 4).toInt()
        val hours = data.substring(4, 6).toInt()
        val minutes = data.substring(6, 8).toInt()

        val timestamp = DateTime.now()
            .copyDayOfMonth(
                month = Month[month],
                dayOfMonth = days,
                hours = hours,
                minutes = minutes,
                seconds = 0,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        return Chunk(timestamp, data.substring(8))
    }
}
