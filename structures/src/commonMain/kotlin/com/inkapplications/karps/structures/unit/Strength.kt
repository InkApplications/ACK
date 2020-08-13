package com.inkapplications.karps.structures.unit

/**
 * S-Meter strength measurement.
 */
inline class Strength(val value: Short)

/**
 * Use a number value as an S-Meter unit.
 */
val Number.strength get() = Strength(toShort())