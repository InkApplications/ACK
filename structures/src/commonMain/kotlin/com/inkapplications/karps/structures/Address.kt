package com.inkapplications.karps.structures

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
}

