package com.inkapplications.karps.parser.chunk.common

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.Chunker
import kotlin.math.min

/**
 * Pop string data off until a control character is reached.
 *
 * @param stopChars Characters to match to indicate the end of this chunk.
 * @param minLength The minimum number of characters before the control character.
 * @param maxLength The maximum number of characters before the control character.
 */
internal class SpanUntilChunker(
    private val stopChars: CharArray,
    private val minLength: Int = 0,
    private val maxLength: Int? = null,
    private val required: Boolean = false,
    private val popControlCharacter: Boolean = false,
): Chunker<String> {
    override fun popChunk(data: String): Chunk<out String> {
        val controlSearchEnd = min(maxLength?.plus(1) ?: data.length, data.length)
        val controlSubstring = data.substring(minLength, controlSearchEnd)
        val controlPosition = controlSubstring.indexOfFirst { it in stopChars }
            .takeIf { it != -1 }
            ?.plus(minLength)

        if (required && controlPosition == null) throw PacketFormatException("Control Character was not found in string. Expected one of <${stopChars.joinToString()}>. Working <$controlSubstring>")

        val chunkEndPosition = controlPosition ?: min(maxLength ?: (data.length), (data.length))
        val chunkData = data.substring(0, chunkEndPosition)
        val remainingData = data.substring(chunkEndPosition.let { if (popControlCharacter && controlPosition != null) it + 1 else it })

        return Chunk(chunkData, remainingData)
    }
}
