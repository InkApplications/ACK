package com.inkapplications.karps.structures

import inkapplications.spondee.spatial.Angle

data class DirectionReport(
    val trajectory: Trajectory,
    val bearing: Angle,
    val quality: QualityReport
)
