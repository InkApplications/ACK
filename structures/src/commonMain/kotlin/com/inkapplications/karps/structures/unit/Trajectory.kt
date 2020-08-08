package com.inkapplications.karps.structures.unit

data class Trajectory(
    val direction: Bearing?,
    val speed: Speed?
)

infix fun Bearing?.at(speed: Speed?) = Trajectory(this, speed)
