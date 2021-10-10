package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
import kotlinx.datetime.Clock

@Suppress("unused")
object TestData {
    val now = Clock.System.now()

    val prototype = AprsPacket.Unknown(
        raw = "".encodeToByteArray(),
        received = now,
        dataTypeIdentifier = '=',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG"),
        digipeaters = emptyList(),
        body = ""
    )
}
