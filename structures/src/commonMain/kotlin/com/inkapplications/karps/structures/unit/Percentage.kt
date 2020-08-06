package com.inkapplications.karps.structures.unit

/**
 * A fractional percentage value stored with hundredth (1%) precision.
 *
 * @param intValue percentage as a whole percentage integer.
 */
inline class Percentage(val intValue: Int) {
    /**
     * Express the value as a percentage as a fraction of 1. ie. `.55f` for `55%`
     *
     * ex.
     *     Percentage(55).fractionalValue // 0.55f
     */
    val fractionalValue get() = intValue / 100f

    override fun toString() = "${intValue}%"
}

/**
 * Express a percentage as a whole number.
 *
 * This is a whole number, not a fraction!
 * ex. `55.percent` is 55% or 0.55
 *
 * @see asPercentage to convert a fractional value such as `.55`
 */
val Number.percent get() = Percentage(toInt())

/**
 * Express with a fractional percentage.
 *
 * This is a fraction value, not a whole percentage!
 * ex: `0.55f.asPercentage` is 55%
 *
 * @see percent to convert a whole percentage value.
 */
val Float.asPercentage get() = (this * 100).percent

/**
 * Express with a fractional percentage.
 *
 * This is a fraction value, not a whole percentage!
 * ex: `0.55f.asPercentage` is 55%
 *
 * @see percent to convert a whole percentage value.
 */
val Double.asPercentage get() = (this * 100).percent
