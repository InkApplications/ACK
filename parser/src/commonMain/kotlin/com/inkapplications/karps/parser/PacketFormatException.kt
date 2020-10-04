package com.inkapplications.karps.parser

/**
 * Thrown by a [PacketTypeParser] if the data is not applicable to the parser.
 */
open class PacketFormatException(message: String? = null, cause: Throwable? = null): IllegalArgumentException(message, cause)

/**
 * Thrown by a [PacketTypeParser] when multiple errors occurred.
 */
class CompositePacketFormatException(
    val causes: List<PacketFormatException>,
    message: String? = null
): PacketFormatException(message)
