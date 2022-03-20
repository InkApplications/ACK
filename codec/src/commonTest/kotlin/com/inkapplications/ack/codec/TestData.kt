package com.inkapplications.ack.codec

import com.inkapplications.ack.codec.timestamp.TimestampModule
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.PacketRoute
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

@Suppress("unused")
internal object TestData {
    val now = Clock.System.now()
    val timestampModule = TimestampModule(
        timezone = TimeZone.UTC,
        clock = object: Clock {
            override fun now(): Instant = now
        }
    )

    val route = PacketRoute(
        source = StationAddress("KE0YOG"),
        destination = StationAddress("KE0YOG"),
        digipeaters = emptyList(),
    )
}
