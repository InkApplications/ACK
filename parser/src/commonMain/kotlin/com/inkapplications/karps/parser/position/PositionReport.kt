package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.unit.Coordinates

internal sealed class PositionReport {
    abstract val coordinates: Coordinates
    abstract val symbol: Symbol

    data class Plain(
        override val coordinates: Coordinates,
        override val symbol: Symbol
    ): PositionReport()

    data class Compressed(
        override val coordinates: Coordinates,
        override val symbol: Symbol,
        val extension: CompressedPositionExtensions<out Any>?
    ): PositionReport()
}

internal val PositionReport.compressedExtension: CompressedPositionExtensions<out Any>? get() {
    return (this as? PositionReport.Compressed)?.extension
}
