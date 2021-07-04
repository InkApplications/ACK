package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Distance
import inkapplications.spondee.spatial.Angle

/**
 * DF-Report Quality
 */
data class QualityReport(
    val number: Short,
    val range: Distance,
    val accuracy: Angle,
)
