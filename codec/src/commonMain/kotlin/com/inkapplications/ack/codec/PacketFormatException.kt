package com.inkapplications.ack.codec

/**
 * Thrown by a [PacketDataParser] if the data is not applicable to the parser.
 */
open class PacketFormatException(message: String? = null, cause: Throwable? = null): IllegalArgumentException(message, cause)

/**
 * Thrown by a [PacketDataParser] when multiple errors occurred.
 */
class CompositePacketFormatException(
    val causes: List<PacketFormatException>,
    message: String? = null
): PacketFormatException(message)
