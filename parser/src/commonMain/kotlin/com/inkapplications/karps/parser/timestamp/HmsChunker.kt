package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Parse Hours/Minutes/Seconds.
 */
class HmsChunker(
    private val clock: Clock = Clock.System,
): Chunker<Instant> {
    override fun popChunk(data: String): Chunk<out Instant> {
        data[6].requireControl('h')

        val hours = data.substring(0, 2).toInt()
        val minutes = data.substring(2, 4).toInt()
        val seconds = data.substring(4, 6).toInt()

        val timestamp = clock.now()
            .withUtcValues(
                hour = hours,
                minute = minutes,
                second = seconds,
                nanosecond = 0
            )

        return Chunk(timestamp, data.substring(7))
    }
}
