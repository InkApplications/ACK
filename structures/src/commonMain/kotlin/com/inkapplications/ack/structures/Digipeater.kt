package com.inkapplications.ack.structures

import com.inkapplications.ack.structures.station.StationAddress

data class Digipeater(
    val address: StationAddress,
    val heard: Boolean = false
) {
    override fun toString() = "${address}${if (heard) "*" else ""}"
}
