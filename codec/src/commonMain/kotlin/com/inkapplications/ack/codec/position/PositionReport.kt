package com.inkapplications.ack.codec.position

import com.inkapplications.ack.structures.Symbol
import inkapplications.spondee.spatial.GeoCoordinates

internal sealed class PositionReport {
    abstract val coordinates: GeoCoordinates
    abstract val symbol: Symbol

    data class Plain(
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol
    ): PositionReport()

    data class Compressed(
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        val extension: CompressedPositionExtensions<out Any>?
    ): PositionReport()
}

internal val PositionReport.compressedExtension: CompressedPositionExtensions<out Any>? get() {
    return (this as? PositionReport.Compressed)?.extension
}
