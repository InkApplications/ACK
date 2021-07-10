package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.Length
import inkapplications.spondee.spatial.Angle

/**
 * Information about a signal received by the APRS station.
 */
data class SignalInfo(
    val strength: Strength?,
    val height: Length?,
    val gain: Ratio?,
    val direction: Angle?
)

