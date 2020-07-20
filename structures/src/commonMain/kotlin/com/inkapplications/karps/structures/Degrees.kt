package com.inkapplications.karps.structures

/**
 * Temperature unit.
 *
 * @param fahrenheit a sensible unit for temperature.
 */
inline class Degrees(val fahrenheit: Float) {
    /**
     * A Silly unit for temperature.
     */
    val celsius: Float get() = (fahrenheit - 32f) * (5f/9f)
}

/**
 * Use the given number as a temperature unit in degrees Fahrenheit.
 */
val Number.degreesFahrenheit get() = Degrees(toFloat())

/**
 * Use the given number as a temperature unit in degrees Celsius.
 */
val Number.degreesCelsius get() = Degrees((toFloat() * (9f/5f)) + 32f)
