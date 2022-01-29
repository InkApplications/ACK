package com.inkapplications.ack.parser

import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData

object UnknownPacketTransformer: PacketDataTransformer {
    override fun parse(body: String): PacketData {
        return PacketData.Unknown(body = body)
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Unknown>()

        return packet.body
    }
}
