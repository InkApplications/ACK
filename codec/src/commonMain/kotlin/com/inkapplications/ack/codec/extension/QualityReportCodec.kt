package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.SimpleCodec
import com.inkapplications.ack.codec.digit
import com.inkapplications.ack.structures.QualityReport
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.measure.us.toMiles
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.toDouble
import kotlin.math.log10
import kotlin.math.pow

/**
 * Transforms a range/quality report, referred to as 'NRQ' in the specification.
 */
internal object QualityReportCodec: SimpleCodec<QualityReport> {
    private const val RANGE_BASE = 2.0
    private const val QUALITY_BASE = 2.0
    private const val QUALITY_OFFSET = 9

    override fun decode(data: String): QualityReport {
        return QualityReport(
            data[0].digit,
            RANGE_BASE.pow(data[1].digit.toInt()).miles,
            QUALITY_BASE.pow(QUALITY_OFFSET - data[2].digit).degrees,
        )
    }

    override fun encode(data: QualityReport): String {
        val n = data.number.toString()[0]
        val r = data.range.toMiles()
            .toDouble()
            .let { log10(it) }
            .div(log10(RANGE_BASE))
            .toInt()
            .toString()
            .get(0)
        val q = data.accuracy.toDegrees()
            .toDouble()
            .let { log10(it) }
            .div(log10(QUALITY_BASE))
            .plus(QUALITY_OFFSET)
            .toInt()
            .toString()
            .get(0)

        return "$n$r$q"
    }
}
