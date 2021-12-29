package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.EncodingConfig

/**
 * Parse APRS Packets.
 */
interface AprsParser {
    /**
     * Parse a packet from an IS string representation.
     */
    fun fromString(packet: String): AprsPacket

    /**
     * Parse a packet from an AX.25 byte packet.
     */
    fun fromAx25(packet: ByteArray): AprsPacket

    /**
     * Encode a packet into a String
     */
    fun toString(packet: AprsPacket, config: EncodingConfig = EncodingConfig()): String

    /**
     * Encode a packet into a AX.25 byte array
     */
    fun toAx25(packet: AprsPacket, config: EncodingConfig = EncodingConfig()): ByteArray
}
