package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Length

/**
 * Information about recent rainfall.
 */
data class RainData(
    val lastHour: Length?,
    val last24hours: Length?,
    val today: Length?
)
