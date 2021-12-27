package com.inkapplications.karps.parser

/**
 * Convert the string to a int value accounting for ambiguity.
 */
internal val String.ambiguousValue: Int
    get() = replace(' ', '0')
        .replace('.', '0')
        .takeIf { it.isNotEmpty() }
        ?.toInt()
        ?: 0

/**
 * Convert the string to an integer, allowing for blank or '.' values.
 */
internal val String.optionalValue: Int? get() = takeIf { !all { it in charArrayOf(' ', '.') } }?.toInt()

/**
 * Get the value of a char starting at Ascii digit of '0'
 */
internal val Char.digit
    get() = minus(48).code.toShort()
        .takeIf { it in 0..9 }
        ?: throw NumberFormatException("Expected a digit char but got <$this>")

/**
 * Get the value of a char starting at Ascii digit of '0'
 */
internal val Char.digitBasedValue get() = minus(48).code.toShort()
