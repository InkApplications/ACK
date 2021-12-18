package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket

/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketTypeParser {
    val dataTypeFilter: CharArray? get() = null
    fun parse(packet: AprsPacket.Unknown): AprsPacket
}

interface PacketGenerator {
    fun generate(packet: AprsPacket): String
}

interface PacketTransformer: PacketTypeParser, PacketGenerator

class UnhandledEncodingException: IllegalArgumentException()
fun PacketGenerator.unhandled(): Nothing = throw UnhandledEncodingException()
