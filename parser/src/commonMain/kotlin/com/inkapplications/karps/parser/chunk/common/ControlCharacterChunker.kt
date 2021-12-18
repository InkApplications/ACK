package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.Chunker

/**
 * Asserts that an expected string is at the current position.
 *
 * @param expected The sequence of expected characters to match.
 */
internal class ControlCharacterChunker(
    private val expected: String
): Chunker<Nothing?> {
    constructor(expected: Char): this(expected.toString())

    override fun popChunk(data: String): Chunk<Nothing?> {
        if (!data.startsWith(expected)) {
            throw PacketFormatException("Expected Control Characters <$expected> not found in <$data>")
        }

        return Chunk(null, data.substring(expected.length))
    }
}
