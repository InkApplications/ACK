package com.inkapplications.karps.structures

import inkapplications.spondee.measure.Length
import inkapplications.spondee.spatial.Angle

/**
 * DF-Report Quality
 */
data class QualityReport(
    val number: Short,
    val range: Length,
    val accuracy: Angle,
)
