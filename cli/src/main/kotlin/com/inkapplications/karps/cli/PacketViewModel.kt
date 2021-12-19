package com.inkapplications.karps.cli

import com.inkapplications.karps.cli.Control.Color.blue
import com.inkapplications.karps.cli.Control.Color.green
import com.inkapplications.karps.cli.Control.Color.lightRed
import com.inkapplications.karps.cli.Control.Color.magenta
import com.inkapplications.karps.cli.Control.Color.yellow
import com.inkapplications.karps.structures.AprsPacket

data class PacketViewModel(
    val packet: AprsPacket,
    val data: String,
) {
    val alias: String = when (packet) {
        is AprsPacket.Position -> blue.span("${packet.route.source}")
        is AprsPacket.Weather -> yellow.span("${packet.route.source}")
        is AprsPacket.ObjectReport -> magenta.span("${packet.route.source}")
        is AprsPacket.ItemReport -> magenta.span("${packet.route.source}")
        is AprsPacket.Message -> green.span("${packet.route.source}")
        is AprsPacket.Unknown -> lightRed.span("${packet.route.source}")
        else -> "${packet.route.source}"
    }

    val message = when (packet) {
        is AprsPacket.Position -> "${packet.coordinates} ${packet.comment}"
        is AprsPacket.Weather -> "${packet.temperature}"
        is AprsPacket.ObjectReport -> "${packet.state.name} ${packet.name}"
        is AprsPacket.ItemReport -> "${packet.state.name} ${packet.name}"
        is AprsPacket.Message -> "-> ${green.span("${packet.addressee}")}: ${packet.message}"
        is AprsPacket.TelemetryReport -> "${packet.sequenceId} ${packet.data}"
        is AprsPacket.StatusReport -> packet.status
        is AprsPacket.CapabilityReport -> packet.capabilityData.toString()
        is AprsPacket.Unknown -> packet.body
    }

    override fun toString(): String = "<$alias> $message"
}
