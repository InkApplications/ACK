package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.chunk.requireControl
import com.inkapplications.ack.codec.extension.DataExtensions.DirectionReportExtra
import com.inkapplications.ack.codec.optionalValue
import com.inkapplications.ack.structures.DirectionReport
import com.inkapplications.ack.structures.at
import inkapplications.spondee.measure.us.knots
import inkapplications.spondee.spatial.degrees

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

        val course = data.substring(0, 3).optionalValue?.degrees
        val speed = data.substring(4, 7).optionalValue?.knots
        val bearing = data.substring(8, 11).toInt().degrees
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
