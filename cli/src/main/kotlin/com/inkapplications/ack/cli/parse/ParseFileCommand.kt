package com.inkapplications.ack.cli.parse

import androidx.compose.runtime.State
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.inkapplications.ack.cli.MosaicCommand
import com.inkapplications.ack.codec.Ack
import com.jakewharton.mosaic.ui.Column
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


class ParseFileCommand: MosaicCommand<ParserState>() {
    private val file by argument(
        help = "The APRS-IS log file to parse into objects"
    ).file()

    private val output by option(
        help = "An output file to write object code to"
    ).file()

    override suspend fun renderPlain(state: ParserState) {
        when (state) {
            is ParserState.Initializing -> echo("Parsing ${state.testFile.name}")
            is ParserState.Running -> {}
            is ParserState.Complete -> {
                echo("Top Unknown Identifier Symbols:")
                state.topUnidentified.forEach { (symbol, count) ->
                    echo ("    $symbol ($count)")
                }
                echo("Passed: ${state.passed} Failed: ${state.failed} Unknown: ${state.unknown}")
                echo("Parse Complete in ${state.time}")
            }
        }
    }

    override suspend fun renderMosaic(state: State<ParserState>) {
        setContent {
            Column { ParseSummary(state.value) }
        }
    }

    override suspend fun start(): ParserState {
        val parser = Ack().defaultParser()
        val lines: List<LineStatus> = file.readLines(Charsets.US_ASCII)
            .filterNot { it.startsWith('#') }
            .map { LineStatus.Todo(it) }

        val complete = AtomicInteger(0)

        state = ParserState.Running(state.testFile, complete.get(), lines.size)

        val results = measureTimedValue {
            lines
                .map { test ->
                    backgroundScope.async {
                        runCatching { LineStatus.Passed(line = test.line, packet = parser.fromString(test.line)) }
                            .getOrElse { LineStatus.Failed(test.line, it) }
                            .also {
                                state = ParserState.Running(file, complete.incrementAndGet(), lines.size)
                            }
                    }
                }
                .awaitAll()
        }

        output?.outputStream()?.bufferedWriter()?.use { writer ->
            results.value.forEach { lineStatus ->
                writer.write(lineStatus.toString())
                writer.write("\n")
            }
        }

        return ParserState.Complete(
            testFile = file,
            tests = results.value,
            time = results.duration,
        )
    }

    private val percentageFormat = DecimalFormat("###.##%")
    private infix fun Int.percentOf(total: Int): String {
        val value = if (total == 0) 0f else (toFloat() / total.toFloat())

        return percentageFormat.format(value)
    }

    override suspend fun initialValue(): ParserState {
        return ParserState.Initializing(file)
    }
}
