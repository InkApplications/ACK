package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*

@Suppress("unused")
object TestData {
    val now = SystemClock.current

    val prototype = AprsPacket.Unknown(
        received = now,
        dataTypeIdentifier = '=',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG"),
        digipeaters = emptyList(),
        body = ""
    )
}
