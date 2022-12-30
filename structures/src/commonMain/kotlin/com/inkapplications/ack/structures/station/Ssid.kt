package com.inkapplications.ack.structures.station

/**
 * Wraps a canonical SSID value.
 */
@JvmInline
value class Ssid(val value: String = "0") {
    /**
     * Assumed SSID type based on recommended categories.
     */
    val type: SsidType
        get() = when (value) {
            "0" -> SsidType.Primary
            "1", "2", "3", "4", "15" -> SsidType.Generic
            "5" -> SsidType.Network
            "6" -> SsidType.Special
            "7" -> SsidType.Ht
            "8" -> SsidType.Boat
            "9" -> SsidType.Mobile
            "10" -> SsidType.Internet
            "11" -> SsidType.Air
            "12" -> SsidType.Tracker
            "13" -> SsidType.Weather
            "14" -> SsidType.Trucking
            else -> SsidType.Unknown
        }

    override fun toString(): String = value
}
