package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.common.CompositeChunker
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

class TimestampChunker(
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.UTC
): Chunker<Instant> by CompositeChunker(
    DhmlChunker(clock, timeZone),
    DhmzChunker(clock),
    HmsChunker(clock),
    MdhmChunker(clock)
)
