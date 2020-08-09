package com.inkapplications.karps.structures.unit

/**
 * Unit of power ratio.
 */
inline class Ratio(val decibels: Short)

/**
 * Use a number as a representation of a power ratio in decibels.
 */
val Number.decibels get() = Ratio(toShort())
