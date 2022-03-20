package com.inkapplications.ack.structures

import com.inkapplications.ack.structures.station.StationAddress

data class PacketRoute(
    val source: StationAddress,
    val destination: StationAddress,
    val digipeaters: List<Digipeater>,
)
