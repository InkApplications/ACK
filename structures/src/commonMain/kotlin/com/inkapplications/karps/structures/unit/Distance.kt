package com.inkapplications.karps.structures.unit

private const val MILES_RATIO = 5280
private const val METERS_RATIO = 0.3048f

/**
 * A Distance measurement.
 *
 * Unlike [Length] this unit is intended to measure large distances at
 * accuracies down to 1 foot.
 */
inline class Distance(val feet: Int) {
    val miles: Float get() = feet / MILES_RATIO.toFloat()
    val meters: Float get() = feet * METERS_RATIO
    val kilometers: Float get() = meters / 1000
}

val Number.feet get() = Distance(toInt())
val Number.miles get() = (toFloat() * MILES_RATIO).feet
val Number.meters get() = (toFloat() / METERS_RATIO).feet
val Number.kilometers get() = (toFloat() / METERS_RATIO / 1000).feet
