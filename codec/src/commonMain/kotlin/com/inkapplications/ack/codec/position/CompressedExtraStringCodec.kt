package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.decode
import com.inkapplications.ack.codec.format.Base91
import com.inkapplications.ack.structures.Trajectory
import com.inkapplications.ack.structures.WindData
import com.inkapplications.ack.structures.at
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.us.*
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.toDouble
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

internal object CompressedExtraStringCodec {
    private const val RANGE_EXTRA_INDICATOR = '{'
    private const val ALTITUDE_BASE = 1.002
    private const val RANGE_MULTIPLIER = 2
    private const val RANGE_BASE = 1.08
    private const val COURSE_MULTIPLIER = 4
    private const val SPEED_BASE = 1.08
    private const val SPEED_OFFSET = 1

    fun encodeAltitude(altitude: Length): String {
        val altitudeExponent = altitude.toFeet().value.toDouble().let { log10(it) / log10(ALTITUDE_BASE) }.roundToInt()
        val c = altitudeExponent.div(Base91.BASE)
        val s = altitudeExponent.minus(c * Base91.BASE)
        val char1 = Base91.encode(c)[0]
        val char2 = Base91.encode(s)[0]

        return "$char1$char2"
    }

    fun decodeExtra(compressedExtra: String): CompressedPositionExtensions<*>? {
        // Spec refers to these three bytes as cs and T, so we'll do the same here for consistency even though it's clunky
        val (c, s, T) = compressedExtra.toCharArray()
        val compressionInfo = T.code.toByte().let(CompressionInfoDeserializer::fromByte)

         return when {
            c == ' ' -> null
            compressionInfo.nemaSource == NemaSourceType.GGA -> c.let(Base91::decode)
                .times(Base91.BASE)
                .plus(s.let(Base91::decode))
                .let { ALTITUDE_BASE.pow(it) }
                .feet
                .let { CompressedPositionExtensions.AltitudeExtra(it) }
             c == RANGE_EXTRA_INDICATOR -> s.let(Base91::decode)
                 .let { RANGE_BASE.pow(it)}
                 .times(RANGE_MULTIPLIER)
                 .miles
                 .let { CompressedPositionExtensions.RangeExtra(it) }
             c in '!'..'z' -> {
                 val bearing = c.let(Base91::decode)
                     .times(COURSE_MULTIPLIER)
                     .degrees
                 val speed = s.let(Base91::decode)
                     .let { SPEED_BASE.pow(it) }
                     .minus(SPEED_OFFSET)
                     .knots
                 CompressedPositionExtensions.TrajectoryExtra(bearing at speed)
             }
            else -> null
        }
    }

    fun encodeRange(range: Length): String {
        val rangeChar = range.toMiles()
            .toDouble()
            .div(RANGE_MULTIPLIER)
            .let { log10(it) }
            .div( log10(RANGE_BASE) )
            .roundToInt()
            .let(Base91::encode)
            .get(0)

        return "$RANGE_EXTRA_INDICATOR$rangeChar"
    }

    fun encodeTrajectory(trajectory: Trajectory): String {
        val course = trajectory.direction
            ?.toDegrees()
            ?.toDouble()
            ?.div(COURSE_MULTIPLIER)
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        val speed = trajectory.speed
            ?.toKnots()
            ?.toDouble()
            ?.plus(SPEED_OFFSET)
            ?.let { log10(it) }
            ?.div(log10(SPEED_BASE))
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        return "$course$speed"
    }

    fun encodeWindData(data: WindData): String {
        val course = data.direction
            ?.toDegrees()
            ?.toDouble()
            ?.div(COURSE_MULTIPLIER)
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        val speed = data.speed
            ?.toMilesPerHourValue()
            ?.plus(SPEED_OFFSET)
            ?.let { log10(it) }
            ?.div(log10(SPEED_BASE))
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        return "$course$speed"
    }
}
