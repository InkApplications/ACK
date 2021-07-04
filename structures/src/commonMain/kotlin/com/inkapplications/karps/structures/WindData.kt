package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Speed
import inkapplications.spondee.spatial.Angle

/**
 * Wraps information about the wind status.
 */
data class WindData(
    val direction: Angle?,
    val speed: Speed?,
    val gust: Speed?
)
