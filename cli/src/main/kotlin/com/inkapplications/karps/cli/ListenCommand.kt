package com.inkapplications.karps.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
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

const val esc: Char = 27.toChar()
const val blue = "${esc}[1;34m"
const val yellow = "${esc}[1;33m"
const val red = "${esc}[0;31m"
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

    private val parser = ParserModule().defaultParser()

    override fun run() {
        runBlocking {
            val client = AprsClientModule.createDataClient()
            client.connect(
                credentials = Credentials(callsign),
                server = server,
                port = port,
                filters = filter
            ) { read, write ->
                read.consumeEach { data ->
                    if (!data.startsWith('#')) parser.fromString(data).also {
                        when (it) {
                            is AprsPacket.Position -> {
                                echo("\n${blue}# Position from: ${it.source}${normal}")
                                echo(" - Coordinates: ${it.coordinates}")
                                echo(" - ${it.comment}")
                            }
                            is AprsPacket.Weather -> {
                                echo("\n${yellow}# Weather from: ${it.source}${normal}")
                                echo(" - Temperature: ${it.temperature}")
                            }
                            is AprsPacket.Unknown -> {
                                echo("\n${red}# Unknown Packet from: ${it.source}${normal}")
                                echo(" - ${it.body}")
                            }
                        }

                    }
                }
            }
        }
    }
}
