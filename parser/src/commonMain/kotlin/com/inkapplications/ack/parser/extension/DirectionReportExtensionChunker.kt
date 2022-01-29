package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.chunk.Chunk
import com.inkapplications.ack.parser.chunk.Chunker
import com.inkapplications.ack.parser.chunk.requireControl
import com.inkapplications.ack.parser.extension.DataExtensions.DirectionReportExtra
import com.inkapplications.ack.parser.optionalValue
import com.inkapplications.ack.structures.DirectionReport
import com.inkapplications.ack.structures.at
import com.inkapplications.ack.structures.unit.Knots
import inkapplications.spondee.spatial.Degrees

/**
 * Parse a direction with quality report extension.
 *
 * This follows the format `CSE/SPD/BRG/NRQ` and allows for omitted values
 * only in course/speed fields.
 */
internal object DirectionReportExtensionChunker: Chunker<DirectionReportExtra> {
    override fun popChunk(data: String): Chunk<DirectionReportExtra> {
        data[3].requireControl('/')
        data[7].requireControl('/')
        data[11].requireControl('/')

        val course = data.substring(0, 3).optionalValue?.let(Degrees::of)
        val speed = data.substring(4, 7).optionalValue?.let { Knots.of(it) }
        val bearing = data.substring(8, 11).toInt().let(Degrees::of)
        val quality = data.substring(12, 15).let {
            QualityReportCodec.decode(it)
        }

        val report = DirectionReport(
            trajectory = course at speed,
            bearing = bearing,
            quality = quality
        )

        return report
            .let(::DirectionReportExtra)
            .let { Chunk(it, data.substring(15)) }
    }
}
