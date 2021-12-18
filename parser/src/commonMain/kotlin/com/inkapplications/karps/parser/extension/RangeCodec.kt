package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.SimpleCodec
import com.inkapplications.karps.parser.chunk.requireStartsWith
import com.inkapplications.karps.parser.format.fixedLength
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
