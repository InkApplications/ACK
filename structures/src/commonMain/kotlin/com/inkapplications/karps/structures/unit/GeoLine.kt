package com.inkapplications.karps.structures.unit

import kotlin.math.abs
import kotlin.math.round

/**
 * A Latitude/Longitude line.
 *
 * This can be expressed as a decimal as part of Lat/Lng lookups
 * or as degrees/minutes/seconds and a Cardinal.
 */
sealed class GeoLine {
    abstract val degrees: Int
    abstract val minutes: Int
    abstract val seconds: Float
    abstract val cardinal: Cardinal
    val decimal: Double get() = (degrees.toDouble() + (minutes.toDouble() / 60.0) + (seconds / 3600.0)) * cardinal.decimalSign
}

/**
 * Latitude/parallel position of a coordinate.
 */
data class Latitude(
    override val degrees: Int,
    override val minutes: Int,
    override val seconds: Float,
    override val cardinal: Cardinal
): GeoLine() {
    constructor(decimal: Double): this(
        degrees = decimal.degrees,
        minutes = decimal.minutes,
        seconds = decimal.seconds,
        cardinal = if (decimal < 0) Cardinal.South else Cardinal.North
    )

    override fun toString(): String = "${degrees}º$minutes'${seconds.format}\"${cardinal.symbol}"
}

/**
 * Longitude/meridian position of a coordinate.
 */
data class Longitude(
    override val degrees: Int,
    override val minutes: Int,
    override val seconds: Float,
    override val cardinal: Cardinal
): GeoLine() {
    constructor(decimal: Double): this(
        degrees = decimal.degrees,
        minutes = decimal.minutes,
        seconds = decimal.seconds,
        cardinal = if (decimal < 0) Cardinal.West else Cardinal.East
    )

    override fun toString(): String = "${degrees}º$minutes'${seconds.format}\"${cardinal.symbol}"
}

private val Double.degrees: Int get() = abs(toInt())
private val Double.minutes: Int get() = ((abs(this) - degrees) * 60).toInt()
private val Double.seconds: Float get() = ((abs(this) - degrees - (minutes / 60.0)) * 3600).toFloat()
private val Float.format: String get() = (round(this * 10) / 10).toString()
