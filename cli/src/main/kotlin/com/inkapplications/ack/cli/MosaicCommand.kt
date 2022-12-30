package com.inkapplications.ack.cli

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.runMosaic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.sample

/**
 * Run a command with Mosaic/Compose rendering.
 */
abstract class MosaicCommand<T>(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false,
    treatUnknownOptionsAsArgs: Boolean = false,
): CliktCommand(
    help= help,
    epilog = epilog,
    name = name,
    invokeWithoutSubcommand = invokeWithoutSubcommand,
    printHelpOnEmptyArgs = printHelpOnEmptyArgs,
    helpTags = helpTags,
    autoCompleteEnvvar = autoCompleteEnvvar,
    allowMultipleSubcommands = allowMultipleSubcommands,
    treatUnknownOptionsAsArgs = treatUnknownOptionsAsArgs,
) {
    protected val plain by option(
        help = "Force plain output text"
    ).flag(default = false)

    protected val backgroundScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val uiScope = CoroutineScope(newSingleThreadContext("ui") + SupervisorJob())
    private val jobState = MutableStateFlow<JobState<T>>(JobState.Initial())
    protected var state: T
        get() = (jobState.value as JobState.Progress<T>).value
        set(value) {
            jobState.value = JobState.Progress(value)
        }

    private lateinit var mosaicScope: MosaicScope
    protected abstract suspend fun renderPlain(state: T)
    protected abstract suspend fun renderMosaic(state: State<T>)
    protected abstract suspend fun initialValue(): T
    protected abstract suspend fun start(): T

    final override fun run() {
        runMosaic {
            mosaicScope = this
            val initial = initialValue()
            jobState.value = JobState.Progress(initial)
            val snapshot = mutableStateOf(initial)

            renderMosaic(snapshot)

            val job = launch {
                jobState.let {
                    if (!plain) it.sample(40) else it
                }.collect {
                    when (it) {
                        is JobState.Initial -> {}
                        is JobState.Progress -> {
                            if (plain) renderPlain(it.value)
                            else snapshot.value = it.value
                        }
                        is JobState.Complete -> {
                            if (plain) renderPlain(it.final)
                            else snapshot.value = it.final
                            cancel()
                        }
                    }
                }
            }

            jobState.value = withContext(Dispatchers.Default) { JobState.Complete(start()) }

            job.join()
        }
    }

    protected fun setContent(content: @Composable () -> Unit) {
        if (plain) return
        mosaicScope.setContent(content)
    }
}

private sealed interface JobState<T> {
    class Initial<T>: JobState<T>
    class Progress<T>(val value: T): JobState<T>
    class Complete<T>(val final: T): JobState<T>
}
