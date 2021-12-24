package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData

object UnknownPacketGenertator: PacketDataGenerator {
    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Unknown>()

        return packet.body
    }
}
