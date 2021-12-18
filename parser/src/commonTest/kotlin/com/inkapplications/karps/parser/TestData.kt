package com.inkapplications.karps.parser

import com.inkapplications.karps.parser.timestamp.TimestampModule
import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
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

    val prototype = AprsPacket.Unknown(
        dataTypeIdentifier = '=',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG"),
        digipeaters = emptyList(),
        body = ""
    )
}
