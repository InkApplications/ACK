package com.inkapplications.karps.structures.unit

private const val RATIO = .621371

/**
 * Unit of speed, stored in miles per hour.
 */
inline class Speed(val milesPerHour: Short) {
    val kilometersPerHour: Short get() = (milesPerHour / RATIO).toShort()

    override fun toString() = "$milesPerHour mph"
}

val Number.mph get() = toShort().let(::Speed)
val Number.kph get() = (toDouble() * RATIO).toShort().let(::Speed)
