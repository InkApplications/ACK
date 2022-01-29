package com.inkapplications.ack.parser.format

import kotlin.math.pow
import kotlin.math.roundToInt

internal fun Float.fixedLength(
    decimals: Int,
    leftDigits: Int? = null,
    round: Boolean = true,
): String {
    val left = if (leftDigits != null) {
        toInt().fixedLength(leftDigits)
    } else {
        toInt().toString()
    }

    val right = this.minus(toInt())
        .times(10.0.pow(decimals).toInt())
        .let { if (round) it.roundToInt() else it.toInt() }

    return when {
        decimals == 0 && round -> roundToInt().toString()
        decimals == 0 -> toInt().toString()
        else -> "$left.$right"
    }
}
