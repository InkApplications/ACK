package com.inkapplications.ack.parser

import com.inkapplications.ack.parser.timestamp.TimestampModule
import com.inkapplications.ack.structures.Address
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
        source = Address("KE0YOG"),
        destination = Address("KE0YOG"),
        digipeaters = emptyList(),
    )
}
