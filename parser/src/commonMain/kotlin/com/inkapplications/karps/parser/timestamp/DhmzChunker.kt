package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Parse Days/Hours/Minutes for UTC times.
 */
class DhmzChunker(
    private val clock: Clock = Clock.System,
): Chunker<Instant> {
    override fun popChunk(data: String): Chunk<out Instant> {
        data[6].requireControl('z')
        val days = data.substring(0, 2).toInt()
        val hours = data.substring(2, 4).toInt()
        val minutes = data.substring(4, 6).toInt()

        val timestamp = clock.now().withUtcValues(
                dayOfMonth = days,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )

        return Chunk(timestamp, data.substring(7))
    }
}
