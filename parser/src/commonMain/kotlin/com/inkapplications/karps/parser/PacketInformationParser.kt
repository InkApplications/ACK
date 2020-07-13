package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket

/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketInformationParser {
    /**
     * Supported Data Type Identifiers that this parser may be able to parse.
     */
    val supportedDataTypes: Array<Char>

    /**
     * Attempt to parse the unknown packet's data.
     *
     * If the format of the body does not match the expected format for this
     * parser, this should throw a [PacketFormatException] as early as possible
     * so that another parser can be attempted.
     *
     * @param packet the unknown packet with metadata already parsed, but an unparsed body.
     * @return A known packet type with expanded data.
     * @throws PacketFormatException if the packet is not parsable by this parser.
     */
    fun parse(packet: AprsPacket.Unknown): AprsPacket
}
