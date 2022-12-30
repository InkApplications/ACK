package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.structures.QualityReport
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.measure.us.toMiles
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.toDouble
import kotlin.test.Test
import kotlin.test.assertEquals

class QualityReportCodecTest {
    @Test
    fun decode() {
        val given = "729"

        val result = QualityReportCodec.decode(given)

        assertEquals(7.toShort(), result.number)
        assertEquals(4.0, result.range.toMiles().toDouble(), 1e-15)
        assertEquals(1.0, result.accuracy.toDegrees().toDouble(), 1e-15)
    }

    @Test
    fun encode() {
        val given = QualityReport(
            number = 7,
            range = 4.miles,
            accuracy = 1.degrees,
        )

        val result = QualityReportCodec.encode(given)

        assertEquals("729", result)
    }
}
