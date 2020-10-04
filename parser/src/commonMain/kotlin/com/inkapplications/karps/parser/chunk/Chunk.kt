package com.inkapplications.karps.parser.chunk

/**
 * Data from a successful parse.
 *
 * @param parsed Data that was parsed from the string.
 * @param remainingData Data leftover after parsing, less [parsed].
 */
data class Chunk<T>(val parsed: T, val remainingData: String)

/**
 * Map The result of a chunk from one type to another.
 *
 * @param mapper Operation for transforming the mapped result data.
 */
inline fun <T, R> Chunk<T>.mapParsed(mapper: (T) -> R): Chunk<R> {
    return Chunk(mapper(parsed), remainingData)
}

/**
 * Require that the parsed result contains no remaining data.
 */
fun Chunk<*>.requireEnd() {
    if (remainingData.isNotEmpty()) throw IllegalStateException("Expected end of data, but found: <$remainingData>")
}
