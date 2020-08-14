package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Bearing
import com.inkapplications.karps.structures.unit.Distance

/**
 * DF-Report Quality
 */
data class QualityReport(
    val number: Short,
    val range: Distance,
    val accuracy: Bearing
)