package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket

/**
 * Parse APRS Packets.
 */
interface AprsParser {
    /**
     * Parse a packet from an IS string representation.
     */
    fun fromString(packet: String): AprsPacket
}
