package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import kotlinx.datetime.*

/**
 * Parse Days/Hours/Minutes for local times.
 */
class DhmlChunker(
    private val clock: Clock = Clock.System,
    private val timezone: TimeZone = TimeZone.currentSystemDefault()
): Chunker<Instant> {
    override fun popChunk(data: String): Chunk<out Instant> {
        data[6].requireControl('/')
        val days = data.substring(0, 2).toInt()
        val hours = data.substring(2, 4).toInt()
        val minutes = data.substring(4, 6).toInt()
        val timestamp = clock.now().toLocalDateTime(timezone)
            .with(
                dayOfMonth = days,
                hour = hours,
                minute = minutes,
                second = 0,
                nanosecond = 0
            )
            .toInstant(timezone)

        return Chunk(timestamp, data.substring(7))
    }
}
