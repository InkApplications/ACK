package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.Base91
import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.Chunk
import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*
import kotlin.math.pow

/**
 * Parse a compressed position.
 */
object CompressedPositionChunker: Chunker<PositionReport.Compressed> {
    override fun popChunk(data: String): Chunk<out PositionReport.Compressed> {
        val tableIdentifier = data[0]
        val latitude = data.substring(1, 5)
            .let(Base91::decode)
            .let { it / 380926.0 }
            .let { 90 - it }
            .let(::Latitude)
        val longitude = data.substring(5, 9)
            .let(Base91::decode)
            .let { it / 190463.0 }
            .let { -180 + it }
            .let(::Longitude)
        val codeIdentifier = data[9]
        val compressionInfo = data[12]
            .toByte()
            .let(CompressionInfoDeserializer::fromByte)

        val extension = when {
            data[10] == ' ' -> null
            compressionInfo.nemaSource == NemaSourceType.GGA -> data[10]
                .let(Base91::decode)
                .times(91)
                .plus(data[11].let(Base91::decode))
                .let { 1.002.pow(it) }
                .feet
                .let { CompressedPositionExtensions.AltitudeExtra(it) }
            data[10] == '{' -> data[11]
                .let(Base91::decode)
                .let { 1.08.pow(it) }
                .times(2)
                .miles
                .let { CompressedPositionExtensions.RangeExtra(it) }
            data[10] in '!'..'z' -> {
                val bearing = data[10]
                    .let(Base91::decode)
                    .times(4)
                    .degreesBearing
                val speed = data[11]
                    .let(Base91::decode)
                    .let { 1.08.pow(it) }
                    .minus(1)
                    .knots
                CompressedPositionExtensions.TrajectoryExtra(bearing at speed)
            }
            else -> null
        }

        return Chunk(
            result = PositionReport.Compressed(
                coordinates = Coordinates(latitude, longitude),
                symbol = symbolOf(tableIdentifier, codeIdentifier),
                extension = extension
            ),
            remainingData = data.substring(13)
        )
    }
}

