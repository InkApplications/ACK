package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.Base91
import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude
import com.inkapplications.karps.structures.unit.toCardinal

internal object PositionDataParser {
    val format = Regex("""(?:([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NnSs])([!-~])([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw])([!-~])|([!-~])([!-|]{4})([!-|]{4})([!-~]))""")

    fun getCoordinates(data: String): Coordinates {
        val result = format.matchEntire(data) ?: throw PacketFormatException("Illegal position data: $data")

        return when {
            result.groupValues[1].isNotEmpty() -> parsePlain(result)
            else -> parseCompressed(result)
        }
    }

    fun getEmbeddedSymbol(data: String): Symbol {
        val result = format.matchEntire(data) ?: throw PacketFormatException("Illegal position data: $data")

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

    private val String.value: Float get() = replace(' ', '0').takeIf { it.isNotEmpty() }?.toFloat() ?: 0.0f

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

    private fun parseCompressed(result: MatchResult): Coordinates {
        return Coordinates(
            latitude = Latitude(90 - (Base91.toInt(result.groupValues[12]) / 380926.0)),
            longitude = Longitude(-180 + (Base91.toInt(result.groupValues[13]) / 190463.0))
        )
    }

}
