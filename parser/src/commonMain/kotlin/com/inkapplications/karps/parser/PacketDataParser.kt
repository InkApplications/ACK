package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.PacketData
/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketDataParser {
    fun parse(body: String): PacketData
}
