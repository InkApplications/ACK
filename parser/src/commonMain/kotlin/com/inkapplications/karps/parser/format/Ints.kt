package com.inkapplications.karps.parser.format

import kotlin.math.min
import kotlin.math.pow

/**
 * Convert an integer to a string of fixed length.
 */
internal fun Int.fixedLength(length: Int) = min(this, 10.0.pow(length).toInt()-1).leftPad(length)

/**
 * Take the current value, or zero if null.
 */
internal fun Int?.orZero() = this ?: 0

/**
 * Add leading 0 digits to the left of an integer when converting it to a string.
 */
internal fun Int.leftPad(length: Int): String {
    val normal = this.toString()
    val padding = (length - normal.length)
        .takeIf { it > 0 }
        ?.let { "0".repeat(it) }
        .orEmpty()

    return padding + normal
}
