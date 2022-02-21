package com.inkapplications.ack.codec

import com.inkapplications.ack.structures.PacketData
/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketDataParser {
    fun parse(body: String): PacketData
}
