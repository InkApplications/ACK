package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.SimpleCodec
import com.inkapplications.ack.parser.chunk.requireStartsWith
import com.inkapplications.ack.parser.digit
import com.inkapplications.ack.parser.format.fixedLength
import com.inkapplications.ack.parser.format.orZero
import com.inkapplications.ack.structures.SignalInfo
import com.inkapplications.ack.structures.unit.Strength
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import inkapplications.spondee.structure.value
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

internal object SignalInfoCodec: SimpleCodec<SignalInfo> {
    private const val PREFIX = "DFS"
    private const val HEIGHT_BASE = 2.0
    private const val HEIGHT_MULTIPLIER = 10
    private const val DIRECTION_MULTIPLIER = 45

    override fun decode(data: String): SignalInfo {
        data.requireStartsWith(PREFIX)
        if (data.length != 7) throw IllegalArgumentException("Signal Info data must be 7 characters")

        return SignalInfo(
            strength = Strength(data[3].digit),
            height = Feet.of(HEIGHT_BASE.pow(data[4].digit.toInt()).times(HEIGHT_MULTIPLIER)),
            gain = Bels.of(Deci, data[5].digit),
            direction = data[6].digit.times(DIRECTION_MULTIPLIER).takeIf { it != 0 }?.let(Degrees::of)
        )
    }

    override fun encode(data: SignalInfo): String {
        val s = data.strength
            ?.value
            ?.toInt()
            .orZero()
            .fixedLength(1)
        val h = data.height
            ?.value(Feet)
            ?.div(HEIGHT_MULTIPLIER)
            ?.let { log10(it) }
            ?.div(log10(HEIGHT_BASE))
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val g = data.gain
            ?.value(Deci, Bels)
            ?.toInt()
            .orZero()
            .fixedLength(1)
        val d = data.direction
            ?.value(Degrees)
            ?.div(DIRECTION_MULTIPLIER)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)

        return "$PREFIX$s$h$g$d"
    }
}
