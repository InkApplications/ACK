package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.chunk.requireStartsWith
import com.inkapplications.ack.codec.digit
import com.inkapplications.ack.codec.digitBasedValue
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.codec.format.orZero
import com.inkapplications.ack.structures.TransmitterInfo
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Watts
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import inkapplications.spondee.structure.value
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object TransmitterInfoCodec: SimpleCodec<TransmitterInfo> {
    private const val PREFIX = "PHG"
    private const val POWER_EXPONENT = 2
    private const val HEIGHT_BASE = 2.0
    private const val HEIGHT_MULTIPLIER = 10
    private const val DIRECTION_MULTIPLIER = 45

    override fun decode(data: String): TransmitterInfo {
        data.requireStartsWith(PREFIX)

        val power = data[3]
            .digit
            .toFloat()
            .pow(POWER_EXPONENT)
            .let(Watts::of)
        val height = data[4]
            .digitBasedValue
            .toInt()
            .let { HEIGHT_BASE.pow(it) }
            .times(HEIGHT_MULTIPLIER)
            .let(Feet::of)
        val gain = data[5]
            .digit
            .toInt()
            .let { Bels.of(Deci, it) }
        val direction = data[6]
            .digit
            .toInt()
            .times(DIRECTION_MULTIPLIER)
            .takeIf { it != 0 }
            ?.let(Degrees::of)

        return TransmitterInfo(power, height, gain, direction)
    }

    override fun encode(data: TransmitterInfo): String {
        val power = data.power
            ?.value(Watts)
            ?.pow(1.0 / POWER_EXPONENT)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val height = data.height
            ?.value(Feet)
            ?.div(HEIGHT_MULTIPLIER)
            ?.let { log10(it) }
            ?.div(log10(HEIGHT_BASE))
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val gain = data.gain
            ?.value(Deci, Bels)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val direction = data.direction
            ?.value(Degrees)
            ?.div(DIRECTION_MULTIPLIER)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)

        return "$PREFIX$power$height$gain$direction"
    }
}
