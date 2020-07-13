package com.inkapplications.karps.parser

/**
 * Thrown by a [PacketInformationParser] if the data is not applicable to the parser.
 */
class PacketFormatException(message: String? = null, cause: Throwable? = null): UnsupportedOperationException(message, cause)
