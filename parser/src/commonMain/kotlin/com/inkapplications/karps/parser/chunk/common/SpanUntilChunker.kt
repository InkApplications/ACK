package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.PacketFormatException
import kotlin.math.min

/**
 * Pop string data off until a control character is reached.
 *
 * This does not pop the control character off of the string, only the
 * data encountered before the string.
 *
 * @param stopChars Characters to match to indicate the end of this chunk.
 * @param minLength The minimum number of characters before the control character.
 * @param maxLength The maximum number of characters before the control character.
 */
class SpanUntilChunker(
    private val stopChars: CharArray,
    private val minLength: Int = 0,
    private val maxLength: Int = Int.MAX_VALUE,
    private val required: Boolean = false
): Chunker<String> {
    override fun popChunk(data: String): Chunk<String> {
        val end = min(maxLength, data.length)
        val workingData = data.substring(minLength, end)
        val controlPosition = workingData.indexOfFirst { it in stopChars }.takeIf { it != -1 }?.plus(minLength)

        if (required && controlPosition == null) throw PacketFormatException("Control Character was not found in string. Expected one of <${stopChars.joinToString()}>")
        val endPosition = controlPosition ?: end

        return Chunk(data.substring(0, endPosition), data.substring(endPosition))
    }
}
