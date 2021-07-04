package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Distance
import com.inkapplications.karps.structures.unit.Power
import com.inkapplications.karps.structures.unit.Ratio
import inkapplications.spondee.spatial.Angle

/**
 * Information about the transmitting station radio.
 */
data class TransmitterInfo(
    val power: Power?,
    val height: Distance?,
    val gain: Ratio?,
    val direction: Angle?
)

