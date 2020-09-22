package com.inkapplications.karps.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.inkapplications.karps.client.AprsClientModule
import com.inkapplications.karps.client.Credentials
import com.inkapplications.karps.parser.ParserModule
import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.karps.structures.AprsPacket
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class ParseFileCommand: CliktCommand() {
    private val file by argument(
        help = "The APRS-IS log file to parse into objects"
    ).file()

    private val output by option(
        help = "An output file to write object code to"
    ).file()

    @OptIn(ExperimentalTime::class)
    override fun run() = runBlocking {
        val parser = ParserModule().defaultParser()
        val lines = file.readLines(Charsets.US_ASCII).filterNot { it.startsWith('#') }
        val (results, time) = measureTimedValue {
            lines.map { runCatching { parser.fromString(it) } }
        }

        echo("Total Lines: ${results.size} parsed in: $time")
        val successful = results.filter { it.isSuccess }
        echo("Successful: ${successful.size} (${successful.size percentOf results.size})")
        val identified = results.filter { it.getOrNull() !is AprsPacket.Unknown }
        echo("Identified: ${identified.size} (${identified.size percentOf results.size})")

        if (output != null) {
            echo("Writing object notation to: ${output!!.name}")
            output!!.writeText(
                results.map { it.toString() }.joinToString("\n")
            )
        }
    }

    private val percentageFormat = DecimalFormat("###.##%")
    private infix fun Int.percentOf(total: Int): String {
        val value = if (total == 0) 0f else (toFloat() / total.toFloat())

        return percentageFormat.format(value)
    }
}
