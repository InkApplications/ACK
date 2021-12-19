package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.extension.QualityReportCodec
import com.inkapplications.karps.parser.extension.RangeCodec
import com.inkapplications.karps.parser.extension.SignalInfoCodec
import com.inkapplications.karps.parser.extension.TransmitterInfoCodec
import com.inkapplications.karps.parser.format.Base91
import com.inkapplications.karps.parser.format.fixedLength
import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.Knots
import inkapplications.spondee.measure.Length
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

object PositionCodec {
    fun encodeBody(
        config: EncodingConfig,
        coordinates: GeoCoordinates,
        symbol: Symbol,
        altitude: Length? = null,
        trajectory: Trajectory? = null,
        range: Length? = null,
        transmitterInfo: TransmitterInfo? = null,
        signalInfo: SignalInfo? = null,
        directionReport: DirectionReport? = null,
    ): String {
        val plainRequired = directionReport != null || signalInfo != null || transmitterInfo != null
        val compressedRequired = altitude != null
        return when {
            config.compression == EncodingPreference.Required && plainRequired -> throw IllegalArgumentException(
                "Position Information Contains non-compressible data."
            )
            config.compression == EncodingPreference.Barred && compressedRequired -> throw IllegalArgumentException(
                "Position Information Contains information that must be compressed"
            )
            plainRequired && compressedRequired -> throw IllegalStateException(
                "Cannot encode packet without data loss."
            )
            plainRequired || (!compressedRequired && config.compression in arrayOf(EncodingPreference.Disfavored, EncodingPreference.Barred)) -> encodePlain(
                coordinates = coordinates,
                symbol = symbol,
                directionReport = directionReport,
                trajectory = trajectory,
                transmitterInfo = transmitterInfo,
                range = range,
                signalInfo = signalInfo,
            )
            else -> encodeCompressed(
                coordinates = coordinates,
                symbol = symbol,
                altitude = altitude,
                trajectory = trajectory,
                range = range,
            )
        }
    }

    private fun encodePlain(
        coordinates: GeoCoordinates,
        symbol: Symbol,
        directionReport: DirectionReport? = null,
        trajectory: Trajectory? = null,
        transmitterInfo: TransmitterInfo? = null,
        range: Length? = null,
        signalInfo: SignalInfo? = null,
    ): String {
        val (table, code) = symbol.toTableCodePair()
        val latitude = PlainPositionStringCodec.encodeLatitude(coordinates.latitude)
        val longitude = PlainPositionStringCodec.encodeLongitude(coordinates.longitude)
        val extra = when {
            directionReport != null -> {
                val cse = directionReport.trajectory.direction?.value(Degrees)?.toInt()?.fixedLength(3) ?: "   "
                val spd = directionReport.trajectory.speed?.value(Knots)?.toInt()?.fixedLength(3) ?: "   "
                val bng = directionReport.bearing.value(Degrees).toInt().fixedLength(3)
                val nrq = QualityReportCodec.encode(directionReport.quality)

                "$cse/$spd/$bng/$nrq"
            }
            signalInfo != null -> SignalInfoCodec.encode(signalInfo)
            trajectory != null -> {
                val dir = trajectory.direction?.value(Degrees)?.roundToInt()?.fixedLength(3) ?: "   "
                val spd = trajectory.speed?.value(Knots)?.roundToInt()?.fixedLength(3) ?: "   "
                "$dir/$spd"
            }
            transmitterInfo != null -> TransmitterInfoCodec.encode(transmitterInfo)
            range != null -> RangeCodec.encode(range)
            else -> ""
        }

        return "$latitude$table$longitude$code$extra"
    }

    private fun encodeCompressed(
        coordinates: GeoCoordinates,
        symbol: Symbol,
        altitude: Length? = null,
        trajectory: Trajectory? = null,
        range: Length? = null,
    ): String {
        val (table, code) = symbol.toTableCodePair()
        val latitude = CompressedPositionStringTransformer.encodeLatitude(coordinates.latitude)
        val longitude = CompressedPositionStringTransformer.encodeLongitude(coordinates.longitude)
        val encodedLocation = "$table$latitude$longitude$code"
        val compressionInfo = Base91.encode(0b0_0_1_10_000)
        val extra = when {
            trajectory != null -> CompressedExtraStringCodec.encodeTrajectory(trajectory)
            range != null -> CompressedExtraStringCodec.encodeRange(range)
            altitude != null -> CompressedExtraStringCodec.encodeAltitude(altitude)
            else -> "  "
        }

        return "$encodedLocation$extra$compressionInfo"
    }
}
