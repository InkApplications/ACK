package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.chunk.requireStartsWith
import com.inkapplications.ack.codec.digit
import com.inkapplications.ack.codec.digitBasedValue
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.codec.format.orZero
import com.inkapplications.ack.structures.TransmitterInfo
import inkapplications.spondee.measure.metric.watts
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.*
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
            .watts
        val height = data[4]
            .digitBasedValue
            .toInt()
            .let { HEIGHT_BASE.pow(it) }
            .times(HEIGHT_MULTIPLIER)
            .feet
        val gain = data[5]
            .digit
            .toInt()
            .scale(Deci)
            .bels
        val direction = data[6]
            .digit
            .toInt()
            .times(DIRECTION_MULTIPLIER)
            .takeIf { it != 0 }
            ?.degrees

        return TransmitterInfo(power, height, gain, direction)
    }

    override fun encode(data: TransmitterInfo): String {
        val power = data.power
            ?.toWatts()
            ?.toDouble()
            ?.pow(1.0 / POWER_EXPONENT)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val height = data.height
            ?.toFeet()
            ?.toDouble()
            ?.div(HEIGHT_MULTIPLIER)
            ?.let { log10(it) }
            ?.div(log10(HEIGHT_BASE))
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val gain = data.gain
            ?.toBels()
            ?.value(Deci)
            ?.toDouble()
            ?.roundToInt()
            .orZero()
            .fixedLength(1)
        val direction = data.direction
            ?.toDegrees()
            ?.div(DIRECTION_MULTIPLIER)
            ?.roundToInt()
            .orZero()
            .fixedLength(1)

        return "$PREFIX$power$height$gain$direction"
    }
}
