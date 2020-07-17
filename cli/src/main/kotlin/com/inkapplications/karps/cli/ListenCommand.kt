package com.inkapplications.karps.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
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

    private val parser = ParserModule().parser()

    override fun run() {
        runBlocking {
            val client = AprsClientModule.createDataClient()
            client.connect(
                credentials = Credentials(callsign),
                server = server,
                port = port
            ) { read, write ->
                read.consumeEach { data ->
                    if (!data.startsWith('#')) parser.fromString(data).also {
                        when (it) {
                            is AprsPacket.Position -> println("$it")
                            else -> println("Unknown: $data")
                        }

                    }
                }
            }
        }
    }
}
