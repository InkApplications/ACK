package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.symbolOf
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.longitude

/**
 * Parse a compressed position.
 */
internal object CompressedPositionChunker: Chunker<PositionReport.Compressed> {
    override fun popChunk(data: String): Chunk<out PositionReport.Compressed> {
        val tableIdentifier = data[0]
        val latitude = data.substring(1, 5).let(CompressedPositionStringTransformer::decodeLatitude)
        val longitude = data.substring(5, 9).let(CompressedPositionStringTransformer::decodeLongitude)
        val codeIdentifier = data[9]
        val extension = CompressedExtraStringCodec.decodeExtra(data.slice(10..12))

        Symbol.Schema.validate(codeIdentifier, tableIdentifier)
        require(latitude.asDecimal >= -90.0 - 1e-10) { "Latitude cannot be less than -90 degrees." }
        require(latitude.asDecimal <= 90.0 + 1e-10) { "Latitude cannot be greater than 90 degrees." }
        require(longitude.asDecimal >= -180.0 - 1e-10) { "Latitude cannot be less than -180 degrees." }
        require(longitude.asDecimal <= 180.0 + 1e-10) { "Latitude cannot be greater than 180 degrees." }

        return Chunk(
            result = PositionReport.Compressed(
                coordinates = GeoCoordinates(latitude, longitude),
                symbol = symbolOf(codeIdentifier, tableIdentifier),
                extension = extension
            ),
            remainingData = data.substring(13)
        )
    }
}
