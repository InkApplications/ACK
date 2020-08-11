package com.inkapplications.karps.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import com.inkapplications.karps.client.AprsClientModule
import com.inkapplications.karps.client.Credentials
import com.inkapplications.karps.parser.ParserModule
import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.karps.structures.AprsPacket
import kimchi.logger.ConsolidatedLogger
import kimchi.logger.EmptyWriter
import kimchi.logger.defaultWriter

const val esc: Char = 27.toChar()
const val blue = "${esc}[1;34m"
const val yellow = "${esc}[1;33m"
const val red = "${esc}[0;31m"
const val lightRed = "${esc}[1;31m"
const val normal = "${esc}[0m"

class ListenCommand: CliktCommand() {
    private val callsign by argument(
        name = "callsign",
        help = "Your personal radio callsign to use for connection"
    )

    private val server: String by option(
        names = *arrayOf("--server"),
        help = "APRS server to connect to."
    ).default("first.aprs.net")

    private val port: Int by option(
        names = *arrayOf("--port"),
        help = "APRS server to connect to."
    ).int().default(10152)

    private val filter by option(
        names = *arrayOf("--filter"),
        help = "Raw filter to specify as a server command."
    ).multiple()

    private val verbose by option(
        names = *arrayOf("--verbose")
    ).flag(default = false)

    override fun run() {
        val writer = if (verbose) defaultWriter else EmptyWriter
        val logger = ConsolidatedLogger(writer)
        val parser = ParserModule().defaultParser(logger)
        runBlocking {
            val client = AprsClientModule.createDataClient()
            client.connect(
                credentials = Credentials(callsign),
                server = server,
                port = port,
                filters = filter
            ) { read, write ->
                read.consumeEach { data ->
                    if (data.startsWith('#')) return@consumeEach
                    runCatching { parser.fromString(data) }
                        .onSuccess { printPacket (it) }
                        .onFailure {
                            echo("\n${red}Parse failed for packet:${normal}")
                            echo(" - $data")
                            echo(" - ${it.message}")
                            it.printStackTrace()
                        }
                }
            }
        }
    }

    private fun printPacket(packet: AprsPacket) = when (packet) {
        is AprsPacket.Position -> {
            echo("\n${blue}# Position from: ${packet.source}${normal}")
            echo(" - Coordinates: ${packet.coordinates}")
            echo(" - ${packet.comment}")
        }
        is AprsPacket.Weather -> {
            echo("\n${yellow}# Weather from: ${packet.source}${normal}")
            echo(" - Temperature: ${packet.temperature}")
        }
        is AprsPacket.Unknown -> {
            echo("\n${lightRed}# Unknown Packet from: ${packet.source}${normal}")
            echo(" - ${packet.body}")
        }
    }
}
