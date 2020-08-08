package com.inkapplications.karps.parser

import kotlin.math.pow

/**
 * Convert values to/from base-91 ascii strings for "compression"
 */
internal object Base91 {
    private const val OFFSET = 33
    private const val BASE = 91

    fun toInt(characters: String): Int {
        return characters
            .map { it - OFFSET }
            .mapIndexed { index, character ->
                character.toDouble() * BASE.toDouble().pow(characters.length - index - 1)
            }
            .map { it.toInt() }
            .sum()
    }

    fun fromInt(value: Int): String {
        val characters = StringBuilder()
        var quotient = value
        do {
            val result = quotient % BASE
            characters.append((result + OFFSET).toChar())
            quotient /= BASE
        } while (quotient > 0)

        return characters.reverse().toString()
    }

    fun toInt(value: Char) = toInt(value.toString())
}

