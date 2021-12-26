package com.inkapplications.karps.parser.format

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Convert an integer to a string of fixed length.
 */
internal fun Int.fixedLength(length: Int): String {
    return when {
        length - (if (this < 0) 1 else 0) <= 0 -> throw IllegalArgumentException("Desired character length is too short")
        this < 0 -> max(this, -10.0.pow(length - 1).toInt() + 1).leftPad(length)
        else -> min(this, 10.0.pow(length).toInt() - 1).leftPad(length)
    }
}

/**
 * Take the current value, or zero if null.
 */
internal fun Int?.orZero() = this ?: 0

/**
 * Add leading 0 digits to the left of an integer when converting it to a string.
 */
internal fun Int.leftPad(length: Int): String {
    val normal = absoluteValue.toString()
    val isNegative = this < 0
    val padding = length.minus(normal.length)
        .minus(if (isNegative) 1 else 0)
        .takeIf { it > 0 }
        ?.let { "0".repeat(it) }
        .orEmpty()
    val negativeSymbol = if (isNegative) "-" else ""

    return negativeSymbol + padding + normal
}
