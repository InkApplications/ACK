package com.inkapplications.ack.structures

import com.inkapplications.ack.structures.unit.Strength
import inkapplications.spondee.measure.Length
import inkapplications.spondee.scalar.Level
import inkapplications.spondee.spatial.Angle

/**
 * Information about a signal received by the APRS station.
 */
data class SignalInfo(
    val strength: Strength?,
    val height: Length?,
    val gain: Level?,
    val direction: Angle?
)

