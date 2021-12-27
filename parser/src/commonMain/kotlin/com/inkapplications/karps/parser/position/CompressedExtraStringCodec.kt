package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.decode
import com.inkapplications.karps.parser.format.Base91
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.at
import com.inkapplications.karps.structures.unit.Knots
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.measure.MilesPerHour
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.value
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
        val altitudeExponent = altitude.value(Feet).let { log10(it) / log10(ALTITUDE_BASE) }.roundToInt()
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
                .let(Feet::of)
                .let { CompressedPositionExtensions.AltitudeExtra(it) }
             c == RANGE_EXTRA_INDICATOR -> s.let(Base91::decode)
                 .let { RANGE_BASE.pow(it)}
                 .times(RANGE_MULTIPLIER)
                 .let(Miles::of)
                 .let { CompressedPositionExtensions.RangeExtra(it) }
             c in '!'..'z' -> {
                 val bearing = c.let(Base91::decode)
                     .times(COURSE_MULTIPLIER)
                     .let(Degrees::of)
                 val speed = s.let(Base91::decode)
                     .let { SPEED_BASE.pow(it) }
                     .minus(SPEED_OFFSET)
                     .let(Knots::of)
                 CompressedPositionExtensions.TrajectoryExtra(bearing at speed)
             }
            else -> null
        }
    }

    fun encodeRange(range: Length): String {
        val rangeChar = range.value(Miles)
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
            ?.value(Degrees)
            ?.div(COURSE_MULTIPLIER)
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        val speed = trajectory.speed
            ?.value(Knots)
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
            ?.value(Degrees)
            ?.div(COURSE_MULTIPLIER)
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        val speed = data.speed
            ?.value(MilesPerHour)
            ?.plus(SPEED_OFFSET)
            ?.let { log10(it) }
            ?.div(log10(SPEED_BASE))
            ?.roundToInt()
            ?.let(Base91::encode)
            ?.get(0)

        return "$course$speed"
    }
}
