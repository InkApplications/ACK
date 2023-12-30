package com.inkapplications.ack.cli.parse

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

@Composable
fun ParseSummary(state: ParserState) = when (state) {
    is ParserState.Initializing -> InitializingLine(state)
    is ParserState.Running -> ProgressLine(state)
    is ParserState.Complete -> CompleteStatusLine(state)
}

@Composable
fun InitializingLine(state: ParserState.Initializing) {
    Row {
        Text("Loading <${state.testFile}>")
    }
}

@Composable
fun ProgressLine(state: ParserState.Running) {
    Row {
        Text("Running: ")
        Text("(${state.complete}/${state.total})")
    }
}

@Composable
fun CompleteStatusLine(state: ParserState.Complete) {
    Row {
        Text("Top Unknown Identifier Symbols:")
    }
    state.topUnidentified.forEach { (symbol, count) ->
        Row {
            Text("    ")
            Text(" $symbol ", background = Color.BrightYellow, color = Color.Black, style = TextStyle.Bold)
            Text(" $count")
        }
    }
    Row {
        Text("Passed:", color = Color.BrightGreen)
        Text(" ${state.passed}")
        Text("  ")
        Text("Failed:", color = Color.BrightRed)
        Text(" ${state.failed}")
        Text("  ")
        Text("Unknown Types:", color = Color.BrightYellow)
        Text(" ${state.unknown}")
    }
    Row {
        Text("Parse Complete", color = Color.Green, style = TextStyle.Bold)
        Text(" in ${state.time}")
    }
}
