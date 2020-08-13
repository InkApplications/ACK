package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.structures.AprsPacket

/**
 * Parse a a position with the first applicable parser.
 *
 * If this is a position type, this parser will require that one of the
 * specified parsers provides a parsed position or this will throw
 * a [PacketFormatException].
 */
class CompositePositionParser(
    private val positionParsers: Array<PacketInformationParser>
): PacketInformationParser {
    override val dataTypeFilter = charArrayOf('!', '/', '@', '=')

    override fun parse(packet: AprsPacket): AprsPacket {
        val result = positionParsers.asSequence()
            .map { it.parse(packet) }
            .firstOrNull { it is AprsPacket.Position }

        return result ?: throw PacketFormatException("Position Required but not found.")
    }
}