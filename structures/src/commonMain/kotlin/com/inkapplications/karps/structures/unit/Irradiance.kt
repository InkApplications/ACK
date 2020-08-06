package com.inkapplications.karps.structures.unit

/**
 * Power received per unit surface area.
 *
 * In APRS, this is the unit used for "Luminosity", however this is not a unit
 * of luminance.
 */
inline class Irradiance(val wattsPerSquareMeter: Int) {
    override fun toString() = "$wattsPerSquareMeter W/m^2"
}

/**
 * Use the given number as a measure of irradiance in watts/meter^2
 */
val Number.wattsPerSquareMeter get() = Irradiance(toInt())
