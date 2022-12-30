package com.inkapplications.ack.structures

import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Power
import inkapplications.spondee.scalar.Level
import inkapplications.spondee.spatial.Angle

/**
 * Information about the transmitting station radio.
 */
data class TransmitterInfo(
    val power: Power?,
    val height: Length?,
    val gain: Level?,
    val direction: Angle?
)

