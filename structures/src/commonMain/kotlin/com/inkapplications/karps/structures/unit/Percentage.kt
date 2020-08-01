package com.inkapplications.karps.structures.unit

/**
 * A fractional percentage value.
 *
 * @param fractionalValue percentage as a fraction of 1. ie. `.55f` for `55%`
 */
inline class Percentage(val fractionalValue: Float) {
    /**
     * Express the value as a whole percentage integer.
     *
     * ex.
     *     Percentage(.55f).intValue // Int(55)
     */
    val intValue get() = (fractionalValue * 100).toInt()
}

/**
 * Express with a fractional percentage.
 *
 * This is a fraction value, not a whole percentage!
 * ex: `0.55f.asPercentage` is 55%
 *
 * @see percent to convert a whole percentage value.
 */
val Float.asPercentage get() = Percentage(toFloat())

/**
 * Express with a fractional percentage.
 *
 * This is a fraction value, not a whole percentage!
 * ex: `0.55f.asPercentage` is 55%
 *
 * @see percent to convert a whole percentage value.
 */
val Double.asPercentage get() = Percentage(toFloat())

/**
 * Express a percentage as a whole number.
 *
 * This is a whole number, not a fraction!
 * ex. `55.percent` is 55% or 0.55
 *
 * @see asPercentage to convert a fractional value such as `.55`
 */
val Number.percent get() = Percentage(toFloat() / 100f)
