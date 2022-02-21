package com.inkapplications.ack.codec.format

/**
 * Adds trailing characters after a string to fill a desired length.
 *
 * @param length The output length of the string
 * @param padding The character to fill space with
 */
internal fun String.rightPad(length: Int, padding: Char = ' '): String {
    val padding = (length - this.length)
        .takeIf { it > 0 }
        ?.let { padding.toString().repeat(it) }
        .orEmpty()

    return this + padding
}

internal fun String.fixedLength(
    length: Int,
    padding: Char = ' ',
): String {
    return if (this.length < length) {
        rightPad(length, padding)
    } else {
        substring(0, length)
    }
}
