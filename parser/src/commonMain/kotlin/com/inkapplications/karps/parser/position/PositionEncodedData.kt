package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.unit.Bearing
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Speed

internal class PositionEncodedData(
    val coordinates: Coordinates,
    val symbol: Symbol,
    val course: Bearing?,
    val speed: Speed?
)
