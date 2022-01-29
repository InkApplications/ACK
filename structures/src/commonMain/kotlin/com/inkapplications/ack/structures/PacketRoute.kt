package com.inkapplications.ack.structures

data class PacketRoute(
    val source: Address,
    val destination: Address,
    val digipeaters: List<Digipeater>,
)
