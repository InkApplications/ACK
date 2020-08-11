package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.parser.PacketInformationParser

/**
 * Parse a position packet.
 */
class PositionParser(
    private val positionParsers: Array<PacketInformationParser>
): PacketInformationParser {
    override val dataTypeFilter = charArrayOf('!', '/', '@', '=')
    override fun parse(data: PacketInformation): PacketInformation {
        val result = positionParsers.fold(data) { data, parser ->
            if (data.position == null) parser.parse(data) else data
        }

        if (result.dataType in dataTypeFilter && result.position == null) {
            throw PacketFormatException("Malformed position")
        }

        return result
    }
}
