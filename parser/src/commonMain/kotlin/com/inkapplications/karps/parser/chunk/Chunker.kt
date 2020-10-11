package com.inkapplications.karps.parser.chunk

import com.inkapplications.karps.structures.AprsPacket

/**
 * Pops "chunks" of data off of a given string.
 */
internal interface Chunker<out T> {
    /**
     * Get an expected piece of data from a string.
     *
     * @param data The data to parse as an arbitrary string.
     * @return The parsed data along with the unused data in the string.
     */
    fun popChunk(data: String): Chunk<out T>
}

/**
 * Create a chunker by mapping the results of another.
 *
 * @param mapper Operation for transforming the mapped result data.
 */
internal inline fun <T, R> Chunker<T>.mapParsed(crossinline mapper: (T) -> R) = object: Chunker<R> {
    override fun popChunk(data: String): Chunk<R> {
        return this@mapParsed.popChunk(data).mapParsed { mapper(it) }
    }
}

/**
 * Start parsing using an unknown APRS Packet Body.
 */
internal fun <T> Chunker<T>.parse(packet: AprsPacket.Unknown): Chunk<out T> {
    return popChunk(packet.body)
}

/**
 * Start parsing using an unknown APRS Packet Body, catching any errors.
 */
internal fun <T> Chunker<T>.parseOptional(packet: AprsPacket.Unknown): Chunk<out T?> {
    return runCatching { popChunk(packet.body) }.getOrNull() ?: Chunk(null, packet.body)
}

/**
 * Parse a chunk using the remaining data after another result.
 *
 * @param result The chunk to start parsing after.
 */
internal fun <T> Chunker<T>.parseAfter(result: Chunk<out Any?>): Chunk<out T> {
    return popChunk(result.remainingData)
}

/**
 * Parse a chunk using the remaining data after another result, catching any errors.
 *
 * @param result The chunk to start parsing after.
 */
internal fun <T> Chunker<T>.parseOptionalAfter(result: Chunk<out Any?>): Chunk<out T?> {
    return runCatching { popChunk(result.remainingData) }.getOrNull() ?: Chunk(null, result.remainingData)
}

/**
 * Assert that a character matches one of the specified values.
 */
internal fun Char.requireControl(vararg allowed: Char) {
    if (this !in allowed) throw IllegalArgumentException("Illegal Control Character. Found <$this> but expected one of: <${allowed.joinToString()}>")
}

/**
 * Require that data starts with a specific sequence of values.
 */
internal fun String.requireStartsWith(allowed: String) {
    if (!startsWith(allowed)) throw IllegalArgumentException("Illegal Control String. Required to start with <$allowed> but got: <$this>")
}

