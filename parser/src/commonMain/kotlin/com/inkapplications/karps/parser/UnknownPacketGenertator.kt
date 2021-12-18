package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.AprsPacket

object UnknownPacketGenertator: PacketGenerator {
    override fun generate(packet: AprsPacket): String = when (packet) {
        is AprsPacket.Unknown -> packet.body
        else -> unhandled()
    }
}
