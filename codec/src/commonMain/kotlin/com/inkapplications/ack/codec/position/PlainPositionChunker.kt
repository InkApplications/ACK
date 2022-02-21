package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.chunk.Chunk
import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.structures.symbolOf
import inkapplications.spondee.spatial.GeoCoordinates

internal object PlainPositionChunker: Chunker<PositionReport.Plain> {
    override fun popChunk(data: String): Chunk<PositionReport.Plain> {
        val tableIdentifier = data[8]
        val codeIdentifier = data[18]
        val latitude = PlainPositionStringCodec.decodeLatitude(data.substring(0..7))
        val longitude = PlainPositionStringCodec.decodeLongitude(data.substring(9..17))

        val coordinates = GeoCoordinates(latitude, longitude)
        val symbol = symbolOf(
            tableIdentifier = tableIdentifier,
            codeIdentifier = codeIdentifier
        )
        val symbolEmbeddedPosition = PositionReport.Plain(coordinates, symbol)

        return Chunk(symbolEmbeddedPosition, data.substring(19))
    }
}
