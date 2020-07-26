package com.inkapplications.karps.structures.unit

private const val RATIO = .621371

inline class Speed(val milesPerHour: Short) {
    val kilometersPerHour: Short get() = (milesPerHour / RATIO).toShort()
}

val Number.mph get() = toShort().let(::Speed)
val Number.kph get() = (toDouble() * RATIO).toShort().let(::Speed)
