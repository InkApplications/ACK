package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.SimpleCodec
import com.inkapplications.ack.parser.chunk.requireStartsWith
import com.inkapplications.ack.parser.format.fixedLength
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

internal object RangeCodec: SimpleCodec<Length> {
    private const val PREFIX = "RNG"

    override fun encode(data: Length): String {
        return "$PREFIX${data.value(Miles).roundToInt().fixedLength(4)}"
    }

    override fun decode(data: String): Length {
        data.requireStartsWith(PREFIX)

        return data.substring(3, 7).toInt().let(Miles::of)
    }
}
