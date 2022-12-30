package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.chunk.requireStartsWith
import com.inkapplications.ack.codec.format.fixedLength
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.measure.us.toMiles
import kotlin.math.roundToInt

internal object RangeCodec: SimpleCodec<Length> {
    private const val PREFIX = "RNG"

    override fun encode(data: Length): String {
        return "$PREFIX${data.toMiles().value.toDouble().roundToInt().fixedLength(4)}"
    }

    override fun decode(data: String): Length {
        data.requireStartsWith(PREFIX)

        return data.substring(3, 7).toInt().miles
    }
}
