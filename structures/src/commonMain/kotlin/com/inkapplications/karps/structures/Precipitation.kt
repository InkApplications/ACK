package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Length

/**
 * Information about recent precipitation.
 */
data class Precipitation(
    val rainLastHour: Length? = null,
    val rainLast24Hours: Length? = null,
    val rainToday: Length? = null,
    val snowLast24Hours: Length? = null
)