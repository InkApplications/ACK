package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.chunk.requireStartsWith
import com.inkapplications.ack.codec.digit
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.codec.format.orZero
import com.inkapplications.ack.structures.SignalInfo
import com.inkapplications.ack.structures.unit.Strength
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.scale
import inkapplications.spondee.structure.toDouble
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
            height = HEIGHT_BASE.pow(data[4].digit.toInt()).times(HEIGHT_MULTIPLIER).feet,
            gain = data[5].digit.scale(Deci).bels,
            direction = data[6].digit.times(DIRECTION_MULTIPLIER).takeIf { it != 0 }?.degrees
        )
    }

    override fun encode(data: SignalInfo): String {
        val s = data.strength
            ?.value
            ?.toInt()
            .orZero()
            .fixedLength(1)
        val h = data.height
            ?.toFeet()
            ?.toDouble()
            ?.div(HEIGHT_MULTIPLIER)
            ?.let { log10(it) }
            ?.div(log10(HEIGHT_BASE))
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val g = data.gain
            ?.toBels()
            ?.value(Deci)
            ?.toInt()
            .orZero()
            .fixedLength(1)
        val d = data.direction
            ?.toDegrees()
            ?.toDouble()
            ?.div(DIRECTION_MULTIPLIER)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)

        return "$PREFIX$s$h$g$d"
    }
}
