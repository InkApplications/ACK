package com.inkapplications.ack.codec

import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig

/**
 * Encodes and Decodes APRS Packets to IS-strings and AX.25 packets.
 */
interface AprsCodec {
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
