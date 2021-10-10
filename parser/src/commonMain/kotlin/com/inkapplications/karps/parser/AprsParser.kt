package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import kotlinx.datetime.Instant

/**
 * Parse APRS Packets.
 */
interface AprsParser {
    /**
     * Parse a packet from an IS string representation.
     */
    fun fromString(packet: String): AprsPacket
    /**
     * Parse a packet from an IS string representation.
     */
    fun fromString(packet: String, received: Instant): AprsPacket

    /**
     * Parse a packet from an AX.25 byte packet.
     */
    fun fromAx25(packet: ByteArray): AprsPacket

    /**
     * Parse a packet from an AX.25 byte packet.
     */
    fun fromAx25(packet: ByteArray, received: Instant): AprsPacket
}
