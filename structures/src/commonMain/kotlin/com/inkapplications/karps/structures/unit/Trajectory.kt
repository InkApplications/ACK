package com.inkapplications.karps.structures.unit

/**
 * Pair of data representing a direction and a speed.
 */
data class Trajectory(
    val direction: Bearing?,
    val speed: Speed?
)

infix fun Bearing?.at(speed: Speed?) = Trajectory(this, speed)
