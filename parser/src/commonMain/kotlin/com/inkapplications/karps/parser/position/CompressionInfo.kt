package com.inkapplications.karps.parser.position

import kotlin.experimental.and

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

/**
 * Parses information out of the "compression type" field from a byte.
 */
internal object CompressionInfoDeserializer {
    fun fromByte(data: Byte) = CompressionInfo(
        gpsFix = when ((data and 0b00_1_00_000).toInt() ushr 5) {
            1 -> FixType.Current
            else -> FixType.Last
        },
        nemaSource = when ((data and 0b00_0_11_000).toInt() ushr 3) {
            0b01 -> NemaSourceType.GLL
            0b10 -> NemaSourceType.GGA
            0b11 -> NemaSourceType.RMC
            else -> NemaSourceType.Other
        },
        origin = when ((data and 0b00_0_00_111).toInt()) {
            0b000 -> CompressionOrigin.Compressed
            0b001 -> CompressionOrigin.TncBText
            0b010 -> CompressionOrigin.Software
            0b100 -> CompressionOrigin.Kpc3
            0b101 -> CompressionOrigin.Pico
            0b111 -> CompressionOrigin.Digipeater
            else ->  CompressionOrigin.Other
        }
    )
}
