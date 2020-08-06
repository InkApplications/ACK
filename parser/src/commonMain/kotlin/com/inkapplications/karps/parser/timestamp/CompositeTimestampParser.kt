package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.unit.Timestamp

/**
 * Delegate timestamp parsing to a series of parsers.
 *
 * This will try parsing with each of the parsers until one is successful.
 * Otherwise, it will throw a [PacketFormatException]
 */
class CompositeTimestampParser(
    private vararg val delegates: TimestampParser
): TimestampParser {
    override fun parse(timestamp: String): Timestamp {
        delegates.forEach { parser ->
            try {
                return parser.parse(timestamp)
            } catch(error: PacketFormatException) {}
        }

        throw PacketFormatException("No parser was able to parse timestamp format: $timestamp")
    }
}
