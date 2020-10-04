package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime

/**
 * Parse Hours/Minutes/Seconds.
 */
class HmsChunker: Chunker<Timestamp> {
    override fun popChunk(data: String): Chunk<out Timestamp> {
        data[6].requireControl('h')

        val hours = data.substring(0, 2).toInt()
        val minutes = data.substring(2, 4).toInt()
        val seconds = data.substring(4, 6).toInt()

        val timestamp = DateTime.now()
            .copyDayOfMonth(
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                milliseconds = 0
            )
            .unixMillisLong
            .asTimestamp

        return Chunk(timestamp, data.substring(7))
    }
}
