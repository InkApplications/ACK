package com.inkapplications.karps.structures.unit

/**
 * Unit of pressure stored with decapascal precision.
 */
inline class Pressure(val decapascals: Int) {
    val pascals: Int get() = decapascals * 10
    val millibars: Int get() = decapascals / 10
    val bars: Float get() = decapascals / 10_000f

    override fun toString() = "${decapascals} mb"
}

/**
 * Use the number as a pressure unit in decapascal.
 */
val Number.decapascals get() = Pressure(toInt())

/**
 * Use the number as a pressure unit in Millibars.
 */
val Number.millibars get() = (toFloat() * 10).decapascals

/**
 * Use the number as a pressure unit in Pascal.
 */
val Number.pascals get() = (toInt() / 10).decapascals

/**
 * Use the number as a pressure unit in Bars.
 */
val Number.bars get() = (toFloat() * 10_000).decapascals
