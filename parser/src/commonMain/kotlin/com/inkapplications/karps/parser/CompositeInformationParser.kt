package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket

/**
 * Combine many information parsers into a single parser.
 *
 * If no parser was able to successfully parse the packet,
 * a [CompositePacketFormatException] will be thrown containing the errors of
 * the previous failures.
 */
class CompositeInformationParser(
    private vararg val delegates: PacketInformationParser
): PacketInformationParser {
    override val supportedDataTypes: CharArray = delegates
        .flatMap { it.supportedDataTypes.toList() }
        .distinct()
        .toCharArray()

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        delegates.filter { packet.dataTypeIdentifier in it.supportedDataTypes }
            .map { parser ->
                try {
                    return parser.parse(packet)
                } catch (error: PacketFormatException) {
                    return@map error
                }
            }
            .run {
                throw CompositePacketFormatException(this, "No parser was able to parse packet.")
            }
    }
}
