package com.inkapplications.karps.structures.unit

private const val RATIO = .254f

/**
 * A small length measurement system, used for measuring rainfall.
 *
 * This is intended to store short lengths at high accuracy, not
 * larger distances like miles.
 */
inline class Length(val hundredthsOfInches: Int) {
    val inches: Float get() = hundredthsOfInches / 100f
    val centimeters: Float get() = hundredthsOfInches * RATIO / 10f
    val millimeters: Float get() = hundredthsOfInches * RATIO
}

/**
 * Convert hundredths of an inch to a length unit.
 */
val Number.hundredthsOfInch get() = Length(toInt())

/**
 * Convert inches to a length unit.
 */
val Number.inches get() = (toFloat() * 100).hundredthsOfInch

/**
 * Convert centimeters to a length unit.
 */
val Number.centimeters get() = (toFloat() / RATIO * 10f).hundredthsOfInch

/**
 * Convert centimeters to a length unit.
 */
val Number.millimeters get() = (toFloat() / RATIO).hundredthsOfInch
