package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.SsidType.*

/**
 * Callsign/SSID pair used in AX.25 to identify a station.
 */
data class Address(
    val callsign: String,
    val ssid: String = "0"
) {
    /**
     * Assumed SSID type based on recommended categories.
     */
    val type: SsidType = when (ssid) {
        "0" -> Primary
        "1", "2", "3", "4", "15" -> Generic
        "5" -> Network
        "6" -> Special
        "7" -> Ht
        "8" -> Boat
        "9" -> Mobile
        "10" -> Internet
        "11" -> Air
        "12" -> Tracker
        "13" -> Weather
        "14" -> Trucking
        else -> Unknown
    }

    override fun toString() = "$callsign-$ssid"
}
