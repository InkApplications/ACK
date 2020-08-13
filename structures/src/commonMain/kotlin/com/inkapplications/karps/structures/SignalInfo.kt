package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.*

/**
 * Information about a signal received by the APRS station.
 */
data class SignalInfo(
    val strength: Strength?,
    val height: Distance?,
    val gain: Ratio?,
    val direction: Bearing?
)

