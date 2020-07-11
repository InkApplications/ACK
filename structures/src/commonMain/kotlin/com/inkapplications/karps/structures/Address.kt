package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.SsidType.*

/**
 * Callsign/SSID pair used in AX.25 to identify a station.
 */
data class Address(
    val callsign: String,
    val ssid: Byte = 0
) {
    init {
        require(ssid in 0..15) { "Address SSID must be 0-15" }
    }

    /**
     * Assumed SSID type based on recommended categories.
     */
    val type: SsidType = when (ssid.toInt()) {
        0 -> Primary
        in 1..4, 15 -> Generic
        5 -> Network
        6 -> Special
        7 -> Ht
        8 -> Boat
        9 -> Mobile
        10 -> Internet
        11 -> Air
        12 -> Tracker
        13 -> Weather
        14 -> Trucking
        else -> throw IllegalArgumentException("SSID must be 0-15")
    }
}
