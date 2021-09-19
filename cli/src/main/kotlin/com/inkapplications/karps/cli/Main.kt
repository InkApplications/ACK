package com.inkapplications.karps.cli

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.inkapplications.karps.cli.parse.ParseFileCommand
import kotlin.system.exitProcess

class Main: NoOpCliktCommand() {
    init {
        subcommands(
            ListenCommand(),
            ParseFileCommand(),
            GeneratePasscodeCommand(),
        )
    }
}

fun main(args: Array<String>) {
    try {
        Main().main(args)
        exitProcess(0)
    } catch (error: Throwable) {
        println("Unknown Error: ${error.message}")
        error.printStackTrace()
        exitProcess(1)
    }
}
