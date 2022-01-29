package com.inkapplications.ack.parser.position

import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlin.test.Test
import kotlin.test.assertEquals

class CompressedPositionStringCodecTest {
    @Test
    fun encode() {
        val result = CompressedPositionStringTransformer.encodeLatitude(49.5.latitude)

        assertEquals("5L!!", result)
    }

    @Test
    fun decode() {
        val input = "5L!!"

        val result = CompressedPositionStringTransformer.decodeLatitude(input)

        assertEquals(49.5, result.asDecimal, 1e-5)
    }

    @Test
    fun encodeLongitude() {
        val input = (-72.75).longitude

        val result = CompressedPositionStringTransformer.encodeLongitude(input)
        assertEquals("<*e7", result)
    }

    @Test
    fun decodeLongitude() {
        val input = "<*e7"

        val result = CompressedPositionStringTransformer.decodeLongitude(input)
        assertEquals(-72.75, result.asDecimal, 1e-5)
    }
}
