package com.inkapplications.karps.parser

/**
 * Check if a substring is entirely numeric.
 *
 * This will check that all characters in a substring are digits.
 * It will not allow decimals.
 */
internal fun CharSequence.substringIsNumeric(startIndex: Int, endIndex: Int): Boolean {
    return substring(startIndex, endIndex).all { it in '0'..'9' }
}
