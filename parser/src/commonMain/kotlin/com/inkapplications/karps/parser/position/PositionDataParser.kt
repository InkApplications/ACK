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
    val format = Regex("""(?:([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NnSs])([!-~])([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw])([!-~])|([!-~])([!-|]{4})([!-|]{4})([!-~])([!-{\s]{2})(.))""")

    fun parse(data: String): PositionEncodedData {
        val result = format.matchEntire(data) ?: throw PacketFormatException("Illegal position data: $data")

        return PositionEncodedData(
            coordinates = getCoordinates(result),
            symbol = getEmbeddedSymbol(result),
            extra = getExtra(result)
        )
    }

    /**
     * Get the compressed extra information.
     *
     * This is determined by three bytes at the end of an compressed position.
     * It can contain either an Altitude, Range or a Trajectory depending on
     * the compression info and the value of the first byte.
     */
    private fun getExtra(result: MatchResult): PositionExtraUnion? {
        val compressionInfo = getCompressionInfo(result)
        return when {
            compressionInfo?.nemaSource == NemaSourceType.GGA -> PositionExtraUnion.Altitude(
                compressionInfo,
                getAltitude(result)
            )
            result.groupValues[15].getOrNull(0) == '{' -> PositionExtraUnion.Range(
                compressionInfo,
                getRange(result)
            )
            result.groupValues[15].getOrNull(0) in '!'..'z' -> PositionExtraUnion.Course(
                compressionInfo,
                getTrajectory(result)
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
            result.groupValues[1].isNotEmpty() -> parsePlain(result)
            else -> parseCompressed(result)
        }
    }

    /**
     * Get the visual symbol encoded in the result.
     */
    private fun getEmbeddedSymbol(result: MatchResult): Symbol {
        return when {
            result.groupValues[5].isNotEmpty() -> symbolOf(
                tableIdentifier = result.groupValues[5].single(),
                codeIdentifier = result.groupValues[10].single()
            )
            else -> symbolOf(
                tableIdentifier = result.groupValues[11].single(),
                codeIdentifier = result.groupValues[14].single()
            )
        }
    }

    /**
     * Parse the compression info byte.
     *
     * This is needed to determine what the extra value indicated in the last
     * compression field, as well as some extra bonus info.
     */
    private fun getCompressionInfo(result: MatchResult): CompressionInfo? {
        val chunk = result.groupValues[16]
        if (chunk.isBlank()) return null
        val data = chunk.single().toByte()

        return CompressionInfoParser.fromByte(data)
    }

    /**
     * Parse a compressed Trajectory.
     *
     * Assuming the compressed extra represents a trajectory, this will parse
     * bearing/speed from the two extra characters or null if blank.
     */
    private fun getTrajectory(result: MatchResult): Trajectory? {
        val chunk = result.groupValues[15]
        if (chunk.isBlank()) return null
        if (chunk[0] !in '!'..'z') return null

        val bearing = (Base91.decode(chunk[0]) * 4).degreesBearing
        val speed = 1.08.pow(Base91.decode(chunk[1]) - 1).minus(1).knots

        return bearing at speed
    }

    /**
     * Parse a compressed altitude.
     *
     * Assuming the compressed extra represents an altitude, this will parse
     * an altitude Distance or null if blank.
     */
    private fun getAltitude(result: MatchResult): Distance? {
        val chunk = result.groupValues[15]
        if (chunk.isBlank()) return null

        return 1.002.pow((Base91.decode(chunk[0]) * 91) + Base91.decode(chunk[1])).feet
    }

    /**
     * Parse a compressed Range.
     *
     * Assuming the compressed extra represents a pre-calculated range, this
     * will parse the distance or null if blank
     */
    private fun getRange(result: MatchResult): Distance? {
        val chunk = result.groupValues[15]
        if (chunk.isBlank()) return null

        return 2.16.pow(Base91.decode(chunk[1])).miles
    }

    /**
     * Convert the string to a float value accounting for ambiguity.
     */
    private val String.value: Float get() = replace(' ', '0').takeIf { it.isNotEmpty() }?.toFloat() ?: 0.0f

    /**
     * Parse an uncompressed position coordinate.
     */
    private fun parsePlain(result: MatchResult): Coordinates {
        val latDegrees = result.groupValues[1].value.toInt()
        val latMinutes = result.groupValues[2].value.toInt()
        val latSeconds = result.groupValues[3].value * .6f
        val latCardinal = result.groupValues[4].single().toCardinal()

        val longDegrees = result.groupValues[6].value.toInt()
        val longMinutes = result.groupValues[7].value.toInt()
        val longSeconds = result.groupValues[8].value * .6f
        val longCardinal = result.groupValues[9].single().toCardinal()

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
            latitude = Latitude(90 - (Base91.decode(result.groupValues[12]) / 380926.0)),
            longitude = Longitude(-180 + (Base91.decode(result.groupValues[13]) / 190463.0))
        )
    }

}
