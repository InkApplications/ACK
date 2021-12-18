package com.inkapplications.karps.parser.position

/**
 * APRS "Compression Type" field.
 *
 * This contains some arbitrary information that is needed to parse the
 * extra compressed data.
 */
internal data class CompressionInfo(
    val gpsFix: FixType,
    val nemaSource: NemaSourceType,
    val origin: CompressionOrigin
)

internal enum class NemaSourceType {
    Other,
    GLL,
    GGA,
    RMC
}

internal enum class CompressionOrigin {
    Compressed,
    TncBText,
    Software,
    Kpc3,
    Pico,
    Digipeater,
    Other
}

internal enum class FixType {
    Last,
    Current
}
