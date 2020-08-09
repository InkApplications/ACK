package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.Base91
import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.at
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.*
import kotlin.math.pow

/**
 * Parse position data from a chunk.
 */
internal object PositionDataParser {
    /**
     * APRS data extension for plain positions.
     */
    private val extension = Regex("""(\d{3}|\s{3}|\.{3})/(\d{3}|\s{3}|\.{3})|PHG(\d{4})|RNG(\d{4})|DFS(\d{4})|T(\d{2})/C(\d{2})""")

    /**
     * Matches a plain position.
     */
    private val plain = Regex("""([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NnSs])([!-~])([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw])([!-~])${extension}""")

    /**
     * Matches a compressed position.
     */
    private val compressed = Regex("""([!-~])([!-|]{4})([!-|]{4})([!-~])([!-{\s]{2})(.)""")

    /**
     * Full format for any position.
     */
    val format = Regex("""(?:${plain}|${compressed})""")

    /**
     * Get a capture group within the compression regex (1-indexed)
     */
    private fun MatchResult.compressedGroup(index: Int) = groupValues[17 + index]

    /**
     * Get a capture group within the plain regex (1-indexed)
     */
    private fun MatchResult.plainGroup(index: Int) = groupValues[index]

    /**
     * Get a capture group within the extension regex used in plain positions. (1-indexed)
     */
    private fun MatchResult.plainExtensionGroup(index: Int) = groupValues[10 + index]

    /**
     * Capture group representing a compressed data extension.
     */
    private val MatchResult.compressedExtension get() = compressedGroup(5)

    /**
     * Whether a string is not blank, empty, or all '.' characters.
     */
    private fun String.isRelevant() = isNotEmpty() && !all { it in charArrayOf(' ', '.') }

    /**
     * Convert the string to a float value accounting for ambiguity.
     */
    private val String.value: Int get() = replace(' ', '0')
        .replace('.', '0')
        .takeIf { it.isNotEmpty() }
        ?.toInt()
        ?: 0

    /**
     * Parse the encoded data out of a position partial match.
     *
     * Use the pattern provided by [format] to match the group required as the
     * input data for this method.
     */
    fun parse(data: String): PositionEncodedData {
        val result = format.matchEntire(data) ?: throw PacketFormatException("Illegal position data: $data")

        return PositionEncodedData(
            coordinates = getCoordinates(result),
            symbol = getEmbeddedSymbol(result),
            extension = getExtension(result)
        )
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
    private fun getExtension(result: MatchResult): PositionExtensionUnion? {
        val compressionInfo = getCompressionInfo(result)
        return when {
            result.plainExtensionGroup(1).isNotEmpty() -> PositionExtensionUnion.Trajectory(
                getPlainTrajectory(result)
            )
            compressionInfo?.nemaSource == NemaSourceType.GGA -> PositionExtensionUnion.Altitude(
                getAltitude(result)
            )
            result.compressedExtension.getOrNull(0) == '{' -> PositionExtensionUnion.Range(
                getRange(result)
            )
            result.compressedExtension.getOrNull(0) in '!'..'z' -> PositionExtensionUnion.Trajectory(
                getCompressedTrajectory(result)
            )
            else -> null
        }
    }

    /**
     * Get the coordinates of the position.
     *
     * This field is required. Although a blank string can be sent as a
     * location, position ambiguity would simply cause this to be parsed as
     * a lat/lng of 0/0.
     */
    private fun getCoordinates(result: MatchResult): Coordinates {
        return when {
            result.plainGroup(1).isNotEmpty() -> parsePlain(result)
            else -> parseCompressed(result)
        }
    }

    /**
     * Get the visual symbol encoded in the result.
     */
    private fun getEmbeddedSymbol(result: MatchResult): Symbol {
        return when {
            result.plainGroup(5).isNotEmpty() -> symbolOf(
                tableIdentifier = result.plainGroup(5).single(),
                codeIdentifier = result.plainGroup(10).single()
            )
            else -> symbolOf(
                tableIdentifier = result.compressedGroup(1).single(),
                codeIdentifier = result.compressedGroup(4).single()
            )
        }
    }

    /**
     * Parse the compression info byte.
     *
     * This is needed to determine what the extension value indicated in the last
     * compression field, as well as some extra bonus info.
     */
    private fun getCompressionInfo(result: MatchResult): CompressionInfo? {
        val chunk = result.compressedGroup(6)
        if (chunk.isBlank()) return null
        val data = chunk.single().toByte()

        return CompressionInfoParser.fromByte(data)
    }

    /**
     * Parse a compressed Trajectory.
     *
     * Assuming the compressed extension represents a trajectory, this will parse
     * bearing/speed from the two extra characters or null if blank.
     */
    private fun getCompressedTrajectory(result: MatchResult): Trajectory? {
        val chunk = result.compressedGroup(5)
        if (chunk.isBlank()) return null
        if (chunk[0] !in '!'..'z') return null

        val bearing = (Base91.decode(chunk[0]) * 4).degreesBearing
        val speed = 1.08.pow(Base91.decode(chunk[1]) - 1).minus(1).knots

        return bearing at speed
    }

    /**
     * Parse a plain Trajectory.
     */
    private fun getPlainTrajectory(result: MatchResult): Trajectory {
        val bearing = result.plainExtensionGroup(1)
            .takeIf { it.isRelevant() }
            ?.value
            ?.degreesBearing
        val speed = result.plainExtensionGroup(2)
            .takeIf { it.isRelevant() }
            ?.value
            ?.mph

        return bearing at speed
    }

    /**
     * Parse a compressed altitude.
     *
     * Assuming the compressed extension represents an altitude, this will parse
     * an altitude Distance or null if blank.
     */
    private fun getAltitude(result: MatchResult): Distance? {
        val chunk = result.compressedExtension
        if (chunk.isBlank()) return null

        return 1.002.pow((Base91.decode(chunk[0]) * 91) + Base91.decode(chunk[1])).feet
    }

    /**
     * Parse a compressed Range.
     *
     * Assuming the compressed extension represents a pre-calculated range, this
     * will parse the distance or null if blank
     */
    private fun getRange(result: MatchResult): Distance? {
        val chunk = result.compressedExtension
        if (chunk.isBlank()) return null

        return 2.16.pow(Base91.decode(chunk[1])).miles
    }

    /**
     * Parse an uncompressed position coordinate.
     */
    private fun parsePlain(result: MatchResult): Coordinates {
        val latDegrees = result.plainGroup(1).value
        val latMinutes = result.plainGroup(2).value
        val latSeconds = result.plainGroup(3).value * .6f
        val latCardinal = result.plainGroup(4).single().toCardinal()

        val longDegrees = result.plainGroup(6).value
        val longMinutes = result.plainGroup(7).value
        val longSeconds = result.plainGroup(8).value * .6f
        val longCardinal = result.plainGroup(9).single().toCardinal()

        val latitude = Latitude(
            degrees = latDegrees,
            minutes = latMinutes,
            seconds = latSeconds,
            cardinal = latCardinal
        )
        val longitude = Longitude(
            degrees = longDegrees,
            minutes = longMinutes,
            seconds = longSeconds,
            cardinal = longCardinal
        )

        return Coordinates(latitude, longitude)
    }

    /**
     * Parse a compressed position coordinate.
     */
    private fun parseCompressed(result: MatchResult): Coordinates {
        return Coordinates(
            latitude = Latitude(90 - (Base91.decode(result.compressedGroup(2)) / 380926.0)),
            longitude = Longitude(-180 + (Base91.decode(result.compressedGroup(3)) / 190463.0))
        )
    }
}
