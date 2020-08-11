package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.Base91
import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.structures.DataExtension
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.at
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.*
import kotlin.math.pow

/**
 * Parse a compressed position.
 */
class CompressedPositionParser: PacketInformationParser {
    private val compressed = Regex("""^([!-~])([!-|]{4})([!-|]{4})([!-~])([!-{\s]{2})(.)""")

    override fun parse(data: PacketInformation): PacketInformation {
        val result = compressed.find(data.body) ?: return data

        val coordinates = Coordinates(
            latitude = Latitude(90 - (Base91.decode(result.groupValues[2]) / 380926.0)),
            longitude = Longitude(-180 + (Base91.decode(result.groupValues[3]) / 190463.0))
        )

        return data.copy(
            body = data.body.substring(13),
            position = coordinates,
            symbol = symbolOf(
                tableIdentifier = result.groupValues[1].single(),
                codeIdentifier = result.groupValues[4].single()
            ),
            extension = result.extension
        )
    }

    private val MatchResult.extensionChunk get() = groupValues[5]

    /**
     * Parse the compression info byte.
     *
     * This is needed to determine what the extension value indicated in the last
     * compression field, as well as some extra bonus info.
     */
    private val MatchResult.compressionInfo: CompressionInfo? get() {
        val chunk = groupValues[6]
        if (chunk.isBlank()) return null
        val data = chunk.single().toByte()

        return CompressionInfoParser.fromByte(data)
    }

    /**
     * Get the position's extension information, if provided.
     *
     * For a plain position, this information appears immediately after the
     * position information.
     * For a compressed position, this is determined by three bytes at the end
     * of an compressed position.
     * It can contain either an Altitude, Range or a Trajectory depending on
     * the compression info and the value of the first byte.
     */
    private val MatchResult.extension: DataExtension? get() {
        return when {
            compressionInfo?.nemaSource == NemaSourceType.GGA -> DataExtension.AltitudeExtra(extraAsAltitude)
            extensionChunk.getOrNull(0) == '{' -> DataExtension.RangeExtra(extraAsRange)
            extensionChunk.getOrNull(0) in '!'..'z' -> DataExtension.TrajectoryExtra(extraAsTrajectory)
            else -> null
        }
    }

    /**
     * Parse a compressed altitude.
     *
     * Assuming the compressed extension represents an altitude, this will parse
     * an altitude Distance or null if blank.
     */
    private val MatchResult.extraAsAltitude: Distance? get() {
        if (extensionChunk.isBlank()) return null

        return 1.002.pow((Base91.decode(extensionChunk[0]) * 91) + Base91.decode(extensionChunk[1])).feet
    }

    /**
     * Parse a compressed Range.
     *
     * Assuming the compressed extension represents a pre-calculated range, this
     * will parse the distance or null if blank
     */
    private val MatchResult.extraAsRange: Distance? get() {
        if (extensionChunk.isBlank()) return null

        return 2.16.pow(Base91.decode(extensionChunk[1])).miles
    }

    /**
     * Parse a compressed Trajectory.
     *
     * Assuming the compressed extension represents a trajectory, this will parse
     * bearing/speed from the two extra characters or null if blank.
     */
    private val MatchResult.extraAsTrajectory: Trajectory? get() {
        if (extensionChunk.isBlank()) return null
        if (extensionChunk[0] !in '!'..'z') return null

        val bearing = (Base91.decode(extensionChunk[0]) * 4).degreesBearing
        val speed = 1.08.pow(Base91.decode(extensionChunk[1]) - 1).minus(1).knots

        return bearing at speed
    }
}
