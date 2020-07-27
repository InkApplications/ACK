package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Bearing
import com.inkapplications.karps.structures.unit.Speed

/**
 * Wraps information about the wind status.
 */
data class WindData(
    val direction: Bearing?,
    val speed: Speed?,
    val gust: Speed?
)
