package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.unit.Coordinates

internal class PositionEncodedData(
    val coordinates: Coordinates,
    val symbol: Symbol,
    val extra: PositionExtraUnion?
)
