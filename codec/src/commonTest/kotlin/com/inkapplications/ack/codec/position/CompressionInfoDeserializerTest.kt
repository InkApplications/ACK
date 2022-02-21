package com.inkapplications.ack.codec.position

import kotlin.test.Test
import kotlin.test.assertEquals

class CompressionInfoDeserializerTest {
    @Test
    fun fromBytes() {
        val data = mapOf(
            0b00_0_00_000 to CompressionInfo(FixType.Last, NemaSourceType.Other, CompressionOrigin.Compressed),
            0b00_1_00_000 to CompressionInfo(FixType.Current, NemaSourceType.Other, CompressionOrigin.Compressed),
            0b00_1_01_000 to CompressionInfo(FixType.Current, NemaSourceType.GLL, CompressionOrigin.Compressed),
            0b00_1_10_000 to CompressionInfo(FixType.Current, NemaSourceType.GGA, CompressionOrigin.Compressed),
            0b00_1_11_000 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Compressed),
            0b00_1_11_001 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.TncBText),
            0b00_1_11_010 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Software),
            0b00_1_11_011 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Other),
            0b00_1_11_100 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Kpc3),
            0b00_1_11_101 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Pico),
            0b00_1_11_110 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Other),
            0b00_1_11_111 to CompressionInfo(FixType.Current, NemaSourceType.RMC, CompressionOrigin.Digipeater)
        )

        data.forEach { (input, expected) ->
            val result = CompressionInfoDeserializer.fromByte(input.toByte())
            assertEquals(expected, result, "Failed to parse input: <$input>")
        }
    }
}
