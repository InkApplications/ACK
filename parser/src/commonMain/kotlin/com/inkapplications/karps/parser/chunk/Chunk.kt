package com.inkapplications.karps.parser.chunk

/**
 * Data from a successful parse.
 *
 * @param result Data that was parsed from the string.
 * @param remainingData Data leftover after parsing, less [result].
 */
internal data class Chunk<T>(val result: T, val remainingData: String)

/**
 * Map The result of a chunk from one type to another.
 *
 * @param mapper Operation for transforming the mapped result data.
 */
internal inline fun <T, R> Chunk<T>.mapParsed(mapper: (T) -> R): Chunk<R> {
    return Chunk(mapper(result), remainingData)
}

/**
 * Require that the parsed result contains no remaining data.
 */
internal fun Chunk<*>.requireEnd() {
    if (remainingData.isNotEmpty()) throw IllegalStateException("Expected end of data, but found: <$remainingData>")
}
