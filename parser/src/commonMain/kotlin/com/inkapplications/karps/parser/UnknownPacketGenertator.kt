package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData

object UnknownPacketGenertator: PacketGenerator {
    override fun generate(packet: PacketData, config: EncodingConfig): String = when (packet) {
        is PacketData.Unknown -> packet.body
        else -> unhandled()
    }
}
