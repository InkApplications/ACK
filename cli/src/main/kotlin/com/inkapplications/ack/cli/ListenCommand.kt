package com.inkapplications.ack.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.inkapplications.ack.client.AprsClientModule
import com.inkapplications.ack.client.Credentials
import com.inkapplications.ack.parser.ParserModule
import kimchi.logger.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.runBlocking

class ListenCommand: CliktCommand() {
    private val callsign by argument(
        name = "callsign",
        help = "Your personal radio callsign to use for connection"
    )

    private val server: String by option(
        names = arrayOf("--server"),
        help = "APRS server to connect to."
    ).default("45.63.21.153")

    private val port: Int by option(
        names = arrayOf("--port"),
        help = "APRS server to connect to."
    ).int().default(10152)

    private val filter by option(
        names = arrayOf("--filter"),
        help = "Raw filter to specify as a server command."
    ).multiple()

    private val verbose by option(
        names = arrayOf("--verbose")
    ).flag(default = false)

    private val debug by option(
        names = arrayOf("--debug")
    ).flag(default = false)

    override fun run() {
        val writer = if (verbose) object: LogWriter by defaultWriter {
            override fun log(level: LogLevel, message: String, cause: Throwable?) {
                defaultWriter.log(level, message, cause)
                cause?.printStackTrace()
            }
        } else EmptyWriter
        val parser = ParserModule(
            logger = ConsolidatedLogger(writer)
        ).defaultParser()
        runBlocking {
            val client = AprsClientModule.createDataClient()
            client.connect(
                credentials = Credentials(callsign),
                server = server,
                port = port,
                filters = filter
            ) { read, write ->
                read.consumeAsFlow()
                    .filterNot { it.startsWith('#') }
                    .collect { data ->
                        runCatching { parser.fromString(data) }
                            .map { PacketViewModel(it, data) }
                            .onSuccess { echo(it.toString()) }
                            .onFailure {
                                if (debug || verbose) {
                                    echo(Control.Color.red.span("\nParse failed for packet:"))
                                    echo(Control.Color.red.span(" - $data"))
                                    echo(Control.Color.red.span(" - ${it.message}"))
                                    it.printStackTrace()
                                }
                            }
                }
            }
        }
    }
}
