package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Bearing
import com.inkapplications.karps.structures.unit.Distance
import com.inkapplications.karps.structures.unit.Power
import com.inkapplications.karps.structures.unit.Ratio

/**
 * Information about the transmitting station radio.
 */
data class TransmitterInfo(
    val power: Power?,
    val height: Distance?,
    val gain: Ratio?,
    val direction: Bearing?
)

