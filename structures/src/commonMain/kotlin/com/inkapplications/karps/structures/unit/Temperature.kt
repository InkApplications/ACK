package com.inkapplications.karps.structures.unit

/**
 * Temperature unit.
 *
 * @param fahrenheit a sensible unit for temperature.
 */
inline class Temperature(val fahrenheit: Float) {
    /**
     * A Silly unit for temperature.
     */
    val celsius: Float get() = (fahrenheit - 32f) * (5f/9f)

    override fun toString(): String = "${fahrenheit}ºF / ${celsius}ºC"
}

/**
 * Use the given number as a temperature unit in degrees Fahrenheit.
 */
val Number.degreesFahrenheit get() = Temperature(toFloat())

/**
 * Use the given number as a temperature unit in degrees Celsius.
 */
val Number.degreesCelsius get() = Temperature((toFloat() * (9f / 5f)) + 32f)