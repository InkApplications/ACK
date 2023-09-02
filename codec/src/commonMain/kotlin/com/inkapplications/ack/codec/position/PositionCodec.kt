package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.extension.QualityReportCodec
import com.inkapplications.ack.codec.extension.RangeCodec
import com.inkapplications.ack.codec.extension.SignalInfoCodec
import com.inkapplications.ack.codec.extension.TransmitterInfoCodec
import com.inkapplications.ack.codec.format.Base91
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.structures.*
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.us.toKnots
import inkapplications.spondee.measure.us.toMilesPerHourValue
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.roundToInt
import inkapplications.spondee.structure.toInt
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
        windData: WindData? = null,
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
                windData = windData,
            )
            else -> encodeCompressed(
                coordinates = coordinates,
                symbol = symbol,
                altitude = altitude,
                trajectory = trajectory,
                range = range,
                windData = windData,
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
        windData: WindData? = null,
    ): String {
        val (id, table) = symbol.toIdTablePair()
        val latitude = PlainPositionStringCodec.encodeLatitude(coordinates.latitude)
        val longitude = PlainPositionStringCodec.encodeLongitude(coordinates.longitude)
        val extra = when {
            directionReport != null -> {
                val cse = directionReport.trajectory.direction?.toDegrees()?.toInt()?.fixedLength(3) ?: "   "
                val spd = directionReport.trajectory.speed?.toKnots()?.toInt()?.fixedLength(3) ?: "   "
                val bng = directionReport.bearing.toDegrees().toInt().fixedLength(3)
                val nrq = QualityReportCodec.encode(directionReport.quality)

                "$cse/$spd/$bng/$nrq"
            }
            signalInfo != null -> SignalInfoCodec.encode(signalInfo)
            trajectory != null -> {
                val dir = trajectory.direction?.toDegrees()?.roundToInt()?.fixedLength(3) ?: "   "
                val spd = trajectory.speed?.toKnots()?.roundToInt()?.fixedLength(3) ?: "   "
                "$dir/$spd"
            }
            windData != null -> {
                val dir = windData.direction?.toDegrees()?.roundToInt()?.fixedLength(3) ?: "   "
                val spd = windData.speed
                    ?.toMilesPerHourValue()
                    ?.roundToInt()
                    ?.fixedLength(3)
                    ?: "   "
                "$dir/$spd"
            }
            transmitterInfo != null -> TransmitterInfoCodec.encode(transmitterInfo)
            range != null -> RangeCodec.encode(range)
            else -> ""
        }

        return "$latitude$table$longitude$id$extra"
    }

    private fun encodeCompressed(
        coordinates: GeoCoordinates,
        symbol: Symbol,
        altitude: Length? = null,
        trajectory: Trajectory? = null,
        range: Length? = null,
        windData: WindData? = null,
    ): String {
        val (id, table) = symbol.toIdTablePair()
        val latitude = CompressedPositionStringTransformer.encodeLatitude(coordinates.latitude)
        val longitude = CompressedPositionStringTransformer.encodeLongitude(coordinates.longitude)
        val encodedLocation = "$table$latitude$longitude$id"
        val compressionInfo = Base91.encode(0b0_0_1_10_000)
        val extra = when {
            trajectory != null -> CompressedExtraStringCodec.encodeTrajectory(trajectory)
            range != null -> CompressedExtraStringCodec.encodeRange(range)
            altitude != null -> CompressedExtraStringCodec.encodeAltitude(altitude)
            windData != null -> CompressedExtraStringCodec.encodeWindData(windData)
            else -> "  "
        }

        return "$encodedLocation$extra$compressionInfo"
    }
}
