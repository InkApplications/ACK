package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Month

/**
 * Parse Month/Day/Hours/Minutes format.
 */
class MdhmChunker(
    private val clock: Clock = Clock.System,
): Chunker<Instant> {
    override fun popChunk(data: String): Chunk<out Instant> {
        val month = data.substring(0, 2).toInt()
        val days = data.substring(2, 4).toInt()
        val hours = data.substring(4, 6).toInt()
        val minutes = data.substring(6, 8).toInt()

        val timestamp = clock.now()
            .withUtcValues(
                month = Month.values()[month - 1],
                dayOfMonth = days,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )

        return Chunk(timestamp, data.substring(8))
    }
}
