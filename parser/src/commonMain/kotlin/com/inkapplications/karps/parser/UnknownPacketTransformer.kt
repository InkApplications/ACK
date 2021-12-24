package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData

object UnknownPacketTransformer: PacketDataTransformer {
    override fun parse(body: String): PacketData {
        return PacketData.Unknown(body = body)
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Unknown>()

        return packet.body
    }
}
