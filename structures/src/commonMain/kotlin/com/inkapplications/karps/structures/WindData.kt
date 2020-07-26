package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Bearing

/**
 * Wraps information about the wind status.
 */
data class WindData(
    val direction: Bearing?
)
