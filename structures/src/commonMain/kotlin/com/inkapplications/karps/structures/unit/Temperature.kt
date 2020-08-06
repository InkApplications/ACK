package com.inkapplications.karps.structures.unit

/**
 * Temperature unit, stored in degrees fahrenheit.
 *
 * @param fahrenheit a sensible unit for temperature.
 */
inline class Temperature(val fahrenheit: Short) {
    /**
     * A Silly unit for temperature.
     */
    val celsius: Float get() = (fahrenheit - 32f) * (5f/9f)

    override fun toString() = "${fahrenheit}ºF"
}

/**
 * Use the given number as a temperature unit in degrees Fahrenheit.
 */
val Number.degreesFahrenheit get() = Temperature(toShort())

/**
 * Use the given number as a temperature unit in degrees Celsius.
 */
val Number.degreesCelsius get() = ((toFloat() * (9f / 5f)) + 32f).degreesFahrenheit
