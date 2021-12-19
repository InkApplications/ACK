package com.inkapplications.karps.cli

import com.inkapplications.karps.cli.Control.Color.blue
import com.inkapplications.karps.cli.Control.Color.green
import com.inkapplications.karps.cli.Control.Color.lightRed
import com.inkapplications.karps.cli.Control.Color.magenta
import com.inkapplications.karps.cli.Control.Color.yellow
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketData

data class PacketViewModel(
    val packet: AprsPacket,
    val data: String,
) {
    val alias: String = when (packet.data) {
        is PacketData.Position -> blue.span("${packet.route.source}")
        is PacketData.Weather -> yellow.span("${packet.route.source}")
        is PacketData.ObjectReport -> magenta.span("${packet.route.source}")
        is PacketData.ItemReport -> magenta.span("${packet.route.source}")
        is PacketData.Message -> green.span("${packet.route.source}")
        is PacketData.Unknown -> lightRed.span("${packet.route.source}")
        else -> "${packet.route.source}"
    }

    val message = when (val data = packet.data) {
        is PacketData.Position -> "${data.coordinates} ${data.comment}"
        is PacketData.Weather -> "${data.temperature}"
        is PacketData.ObjectReport -> "${data.state.name} ${data.name}"
        is PacketData.ItemReport -> "${data.state.name} ${data.name}"
        is PacketData.Message -> "-> ${green.span("${data.addressee}")}: ${data.message}"
        is PacketData.TelemetryReport -> "${data.sequenceId} ${data.data}"
        is PacketData.StatusReport -> data.status
        is PacketData.CapabilityReport -> data.capabilityData.toString()
        is PacketData.Unknown -> data.body
    }

    override fun toString(): String = "<$alias> $message"
}
