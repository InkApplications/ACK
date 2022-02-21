package com.inkapplications.ack.parser.format

import com.inkapplications.ack.parser.SimpleCodec
import kotlin.math.pow

/**
 * Convert values to/from base-91 ascii strings for "compression"
 */
internal object Base91: SimpleCodec<Int> {
    const val OFFSET = 33
    const val BASE = 91

    override fun decode(data: String): Int {
        return data
            .map { it - OFFSET }
            .mapIndexed { index, character ->
                character.code.toDouble() * BASE.toDouble().pow(data.length - index - 1)
            }
            .map { it.toInt() }
            .sum()
    }

    override fun encode(data: Int): String {
        val characters = StringBuilder()
        var quotient = data
        do {
            val result = quotient % BASE
            characters.append((result + OFFSET).toChar())
            quotient /= BASE
        } while (quotient > 0)

        return characters.reverse().toString()
    }
}

