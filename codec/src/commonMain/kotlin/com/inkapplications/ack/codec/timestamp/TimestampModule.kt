package com.inkapplications.ack.codec.timestamp

import com.inkapplications.ack.codec.chunk.common.CompositeChunker
import com.inkapplications.ack.codec.chunk.common.FixedLengthChunker
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

internal class TimestampModule(
    timezone: TimeZone = TimeZone.currentSystemDefault(),
    clock: Clock = Clock.System,
) {
    val dhmlCodec = DhmlCodec(clock, timezone)
    val dhmlChunker = FixedLengthChunker(dhmlCodec, 7)

    val dhmzCodec = DhmzCodec(clock)
    val dhmzChunker = FixedLengthChunker(dhmzCodec, 7)

    val hmsCodec = HmsCodec(clock)
    val hmsChunker = FixedLengthChunker(hmsCodec, 7)

    val mdhmCodec = MdhmCodec(clock)
    val mdhmChunker = FixedLengthChunker(mdhmCodec, 8)

    val timestampChunker = CompositeChunker(
        dhmlChunker,
        dhmzChunker,
        hmsChunker,
        mdhmChunker,
    )
}
