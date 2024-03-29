package com.inkapplications.ack.codec.chunk.common

import com.inkapplications.ack.codec.PacketFormatException
import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker

/**
 * Asserts that an expected string is at the current position.
 *
 * @param expected The sequence of expected characters to match.
 */
internal class ControlCharacterChunker(
    private val expected: Array<String>,
): Chunker<String> {
    constructor(vararg expected: Char): this(expected.map { it.toString() }.toTypedArray())
    constructor(expected: String): this(arrayOf(expected))
    constructor(expected: Char): this(arrayOf(expected.toString()))

    override fun popChunk(data: String): Chunk<String> {
        val match = expected.firstOrNull { data.startsWith(it) }

        if (match == null) {
            throw PacketFormatException("Expected Control Characters <$expected> not found in <$data>")
        }

        return Chunk(match, data.substring(match.length))
    }
}
