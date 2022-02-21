package com.inkapplications.ack.parser

import com.inkapplications.ack.structures.PacketData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Okio
import org.junit.Test
import java.io.File

class FunctionalTests {
    val parser = Ack().defaultParser()
    val adapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(CoverageResults::class.java)
        .indent("    ")

    @Test
    fun coverage() {
        val resultsList = javaClass.classLoader.getResourceAsStream("sample.log")
            .bufferedReader(Charsets.US_ASCII)
            .lineSequence()
            .filterNot { it.startsWith('#') }
            .map { runCatching { parser.fromString(it) } }
            .toList()

        val newResults = CoverageResults(
            total = resultsList.size,
            failed = resultsList.count { it.isFailure },
            unknown = resultsList.count { it.getOrNull()?.data is PacketData.Unknown }
        )

        val recordFile = File("coverage.json").also { it.createNewFile() }
        val recordedResults = recordFile
            .let(Okio::source)
            .let(Okio::buffer)
            .let { runCatching { adapter.fromJson(it) } }
            .getOrNull()

        when {
            recordedResults == null -> {
                recordFile.writeText(adapter.toJson(newResults))
            }
            newResults.total != recordedResults.total -> {
                throw AssertionError("Result set has changed. Manually delete the file if this is intentional.")
            }
            newResults.failed <= recordedResults.failed && newResults.unknown <= recordedResults.unknown -> {
                recordFile.writeText(adapter.toJson(newResults))
            }
            else -> {
                throw AssertionError("Parsing regression! Failed: ${recordedResults.failed}->${newResults.failed} Unknown: ${recordedResults.unknown}->${newResults.unknown}")
            }
        }
    }
}

data class CoverageResults(
    val total: Int,
    val failed: Int,
    val unknown: Int
)
