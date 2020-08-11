package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.ambiguousValue
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude
import com.inkapplications.karps.structures.unit.toCardinal

class PlainPositionParser: PacketInformationParser {
    override val dataTypeFilter = charArrayOf('!', '/', '@', '=')
    private val plain = Regex("""^([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NnSs])([!-~])([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EeWw])([!-~])""")

    override fun parse(data: PacketInformation): PacketInformation {
        val result = plain.find(data.body) ?: return data

        val latDegrees = result.groupValues[1].ambiguousValue
        val latMinutes = result.groupValues[2].ambiguousValue
        val latSeconds = result.groupValues[3].ambiguousValue * .6f
        val latCardinal = result.groupValues[4].single().toCardinal()

        val longDegrees = result.groupValues[6].ambiguousValue
        val longMinutes = result.groupValues[7].ambiguousValue
        val longSeconds = result.groupValues[8].ambiguousValue * .6f
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

        val coordinates = Coordinates(latitude, longitude)

        return data.copy(
            body = data.body.substring(19),
            position = coordinates,
            symbol = symbolOf(
                tableIdentifier = result.groupValues[5].single(),
                codeIdentifier = result.groupValues[10].single()
            )
        )
    }
}
