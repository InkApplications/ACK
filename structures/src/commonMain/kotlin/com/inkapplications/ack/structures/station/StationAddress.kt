package com.inkapplications.ack.structures.station

import com.inkapplications.ack.structures.station.SsidType.*

/**
 * Callsign/SSID pair used in AX.25 to identify a station.
 */
data class StationAddress(
    val callsign: Callsign,
    val ssid: Ssid,
) {
    constructor(callsign: Callsign): this(callsign, Ssid())
    constructor(callsign: String, ssid: String): this(Callsign(callsign), Ssid(ssid))
    constructor(callsign: String): this(Callsign(callsign), Ssid())
    /**
     * Assumed SSID type based on recommended categories.
     */
    val type: SsidType = ssid.type

    override fun toString() = if (type == Primary) callsign.toString() else "$callsign-$ssid"
}

/**
 * Parse a string into a station address.
 */
fun String.toStationAddress(): StationAddress {
    return trim().let {
        StationAddress(
            callsign = it.substringBefore('-'),
            ssid = it.substringAfter('-', "").takeIf { it.isNotEmpty() } ?: "0"
        )
    }
}
