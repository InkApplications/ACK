package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Speed
import inkapplications.spondee.spatial.Angle

/**
 * Pair of data representing a direction and a speed.
 */
data class Trajectory(
    val direction: Angle?,
    val speed: Speed?
)

infix fun Angle?.at(speed: Speed?) = Trajectory(this, speed)
