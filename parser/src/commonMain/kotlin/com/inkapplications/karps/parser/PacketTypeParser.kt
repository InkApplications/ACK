package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketRoute

/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketTypeParser {
    fun parse(route: PacketRoute, body: String): AprsPacket
}

interface PacketGenerator {
    fun generate(packet: AprsPacket): String
}

interface PacketTransformer: PacketTypeParser, PacketGenerator

class UnhandledEncodingException: IllegalArgumentException()
fun PacketGenerator.unhandled(): Nothing = throw UnhandledEncodingException()
