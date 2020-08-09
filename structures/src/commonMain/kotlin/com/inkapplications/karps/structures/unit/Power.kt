package com.inkapplications.karps.structures.unit

/**
 * Unit of power.
 */
inline class Power(val watts: Short)

/**
 * Use a number as a representation of power in watts.
 */
val Number.watts get() = Power(toShort())
