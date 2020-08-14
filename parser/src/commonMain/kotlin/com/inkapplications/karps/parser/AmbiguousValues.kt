package com.inkapplications.karps.parser

/**
 * Convert the string to a int value accounting for ambiguity.
 */
internal val String.ambiguousValue: Int get() = replace(' ', '0')
    .replace('.', '0')
    .takeIf { it.isNotEmpty() }
    ?.toInt()
    ?: 0

/**
 * Convert the string to an integer, allowing for blank or '.' values.
 */
internal val String.optionalValue: Int? get() = takeIf { !all { it in charArrayOf(' ', '.') } }?.toInt()

/**
 * Whether a string is not blank, empty, or all '.' characters.
 */
internal fun String.isRelevant() = isNotEmpty() && !all { it in charArrayOf(' ', '.') }
