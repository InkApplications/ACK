package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.SimpleCodec
import com.inkapplications.karps.parser.digit
import com.inkapplications.karps.structures.QualityReport
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.structure.value
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
            Miles.of(RANGE_BASE.pow(data[1].digit.toInt())),
            Degrees.of(QUALITY_BASE.pow(QUALITY_OFFSET - data[2].digit))
        )
    }

    override fun encode(data: QualityReport): String {
        val n = data.number.toString()[0]
        val r = data.range.value(Miles)
            .let { log10(it) }
            .div(log10(RANGE_BASE))
            .toInt()
            .toString()
            .get(0)
        val q = data.accuracy.value(Degrees)
            .let { log10(it) }
            .div(log10(QUALITY_BASE))
            .plus(QUALITY_OFFSET)
            .toInt()
            .toString()
            .get(0)

        return "$n$r$q"
    }
}
