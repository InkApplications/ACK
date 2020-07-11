package com.inkapplications.karps.structures

/**
 * Geographic Direction
 */
enum class Cardinal(val symbol: Char, internal val decimalSign: Int) {
    North('N', 1),
    East('E', 1),
    South('S', -1),
    West('W', -1)
}

/**
 * Convert a single character to a cardinal direction.
 */
fun Char.toCardinal(): Cardinal = Cardinal.values().first { it.symbol == this }
