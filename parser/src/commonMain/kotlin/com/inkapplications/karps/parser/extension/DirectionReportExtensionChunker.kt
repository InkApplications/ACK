package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.parser.chunk.requireControl
import com.inkapplications.karps.parser.digit
import com.inkapplications.karps.parser.extension.DataExtensions.DirectionReportExtra
import com.inkapplications.karps.parser.optionalValue
import com.inkapplications.karps.structures.DirectionReport
import com.inkapplications.karps.structures.QualityReport
import com.inkapplications.karps.structures.at
import com.inkapplications.karps.structures.unit.degreesBearing
import com.inkapplications.karps.structures.unit.knots
import com.inkapplications.karps.structures.unit.miles
import kotlin.math.pow

/**
 * Parse a direction with quality report extension.
 *
 * This follows the format `CSE/SPD/BRG/NRQ` and allows for omitted values
 * only in course/speed fields.
 */
object DirectionReportExtensionChunker: Chunker<DirectionReportExtra> {
    override fun popChunk(data: String): Chunk<DirectionReportExtra> {
        data[3].requireControl('/')
        data[7].requireControl('/')
        data[11].requireControl('/')

        val course = data.substring(0, 3).optionalValue?.degreesBearing
        val speed = data.substring(4, 7).optionalValue?.knots
        val bearing = data.substring(8, 11).toInt().degreesBearing
        val quality = data.substring(12, 15).let {
            QualityReport(
                it[0].digit,
                2.0.pow(it[1].digit.toInt()).miles,
                2.0.pow(9 - it[2].digit).degreesBearing
            )
        }

        return DirectionReport(
                trajectory = course at speed,
                bearing = bearing,
                quality = quality
            )
            .let(::DirectionReportExtra)
            .let { Chunk(it, data.substring(15)) }
    }
}
