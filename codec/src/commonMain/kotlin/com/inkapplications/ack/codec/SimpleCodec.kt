package com.inkapplications.ack.codec

/**
 * Encodes/Decodes a piece of data [T] to/from a string representation.
 */
internal interface SimpleCodec<T> {
    fun encode(data: T): String
    fun decode(data: String): T
}

internal fun <T> SimpleCodec<T>.decode(value: Char) = decode(value.toString())
