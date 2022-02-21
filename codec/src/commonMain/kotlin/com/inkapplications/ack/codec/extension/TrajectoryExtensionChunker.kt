package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.chunk.requireControl
import com.inkapplications.ack.codec.extension.DataExtensions.TrajectoryExtra
import com.inkapplications.ack.codec.optionalValue
import com.inkapplications.ack.structures.at
import com.inkapplications.ack.structures.unit.Knots
import inkapplications.spondee.spatial.Degrees

/**
 * Parse a bearing/speed extension.
 *
 * This covers both the Course/Speed and the Wind Direction/Speed extension
 * types, since they are identical.
 *
 * These follow the format: `DIR/SPD` and allow unspecified values
 * with spaces or dots. ex: `   /...` is unspecified.
 */
internal object TrajectoryExtensionChunker: Chunker<TrajectoryExtra> {
    override fun popChunk(data: String): Chunk<TrajectoryExtra> {
        data[3].requireControl('/')
        val bearing = data.substring(0, 3).optionalValue?.let(Degrees::of)
        val speed = data.substring(4, 7).optionalValue?.let { Knots.of(it) }

        return TrajectoryExtra(bearing at speed)
            .let { Chunk(it, data.substring(7)) }
    }
}
