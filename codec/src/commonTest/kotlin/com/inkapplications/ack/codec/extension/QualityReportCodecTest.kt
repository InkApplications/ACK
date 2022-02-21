package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.QualityReport
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.spatial.Degrees
import kotlin.test.Test
import kotlin.test.assertEquals

class QualityReportCodecTest {
    @Test
    fun decode() {
        val given = "729"

        val result = QualityReportCodec.decode(given)

        assertEquals(7.toShort(), result.number)
        assertEquals(Miles.of(4), result.range)
        assertEquals(Degrees.of(1), result.accuracy)
    }

    @Test
    fun encode() {
        val given = QualityReport(
            number = 7,
            range = Miles.of(4),
            accuracy = Degrees.of(1),
        )

        val result = QualityReportCodec.encode(given)

        assertEquals("729", result)
    }
}
