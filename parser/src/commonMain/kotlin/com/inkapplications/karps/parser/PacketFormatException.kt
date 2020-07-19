package com.inkapplications.karps.parser

/**
 * Thrown by a [PacketInformationParser] if the data is not applicable to the parser.
 */
open class PacketFormatException(message: String? = null, cause: Throwable? = null): IllegalArgumentException(message, cause)

/**
 * Thrown by a [PacketInformationParser] when multiple errors occurred.
 */
class CompositePacketFormatException(
    val causes: List<PacketFormatException>,
    message: String? = null
): PacketFormatException(message)
