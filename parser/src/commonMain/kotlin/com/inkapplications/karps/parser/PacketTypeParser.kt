package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*

/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketTypeParser {
    val dataTypeFilter: CharArray? get() = null
    fun parse(packet: AprsPacket.Unknown): AprsPacket
}
