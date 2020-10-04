package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.common.CompositeChunker
import com.inkapplications.karps.structures.unit.Timestamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimezoneOffset

class TimestampChunker(
    private val timezone: TimezoneOffset = TimezoneOffset.local(DateTime.now())
): Chunker<Timestamp> by CompositeChunker(
    DhmlChunker(timezone),
    DhmzChunker(),
    HmsChunker(),
    MdhmChunker()
)
