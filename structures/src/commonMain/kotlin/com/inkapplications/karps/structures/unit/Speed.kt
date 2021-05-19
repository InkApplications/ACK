package com.inkapplications.karps.structures.unit

import kotlin.math.roundToInt

private const val KM_RATIO = .621371
private const val KNOTS_RATIO = 1.15078

/**
 * Unit of speed, stored in miles per hour.
 */
inline class Speed(val milesPerHour: Short) {
    val kilometersPerHour: Short get() = (milesPerHour / KM_RATIO).toInt().toShort()
    val knots: Float get() = (milesPerHour / KNOTS_RATIO).toFloat()

    override fun toString() = "$milesPerHour mph"
}

val Number.mph get() = toShort().let(::Speed)
val Number.kph get() = (toDouble() * KM_RATIO).roundToInt().mph
val Number.knots get() = (toDouble() * KNOTS_RATIO).roundToInt().mph
