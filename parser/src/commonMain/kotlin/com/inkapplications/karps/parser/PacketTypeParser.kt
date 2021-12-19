package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData

/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketTypeParser {
    fun parse(body: String): PacketData
}

interface PacketGenerator {
    fun generate(packet: PacketData, config: EncodingConfig = EncodingConfig()): String
}

interface PacketTransformer: PacketTypeParser, PacketGenerator

class UnhandledEncodingException: IllegalArgumentException()
fun PacketGenerator.unhandled(): Nothing = throw UnhandledEncodingException()
