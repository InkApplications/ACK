package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Bearing

data class DirectionReport(
    val trajectory: Trajectory,
    val bearing: Bearing?,
    val quality: QualityReport?
)
