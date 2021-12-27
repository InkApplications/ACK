package com.inkapplications.karps.structures

import inkapplications.spondee.measure.Speed
import inkapplications.spondee.spatial.Angle

/**
 * Wraps information about the wind status.
 */
data class WindData(
    val direction: Angle? = null,
    val speed: Speed? = null,
    val gust: Speed? = null,
)
