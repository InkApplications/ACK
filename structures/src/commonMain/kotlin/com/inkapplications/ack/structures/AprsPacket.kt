package com.inkapplications.ack.structures

/**
 * A Single APRS record.
 */
data class AprsPacket(
    val route: PacketRoute,
    val data: PacketData,
)
