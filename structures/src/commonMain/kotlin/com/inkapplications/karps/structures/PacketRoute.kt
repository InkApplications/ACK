package com.inkapplications.karps.structures

data class PacketRoute(
    val source: Address,
    val destination: Address,
    val digipeaters: List<Digipeater>,
)
