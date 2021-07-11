package com.inkapplications.karps.structures.unit

import inkapplications.spondee.measure.MetersPerSecond
import inkapplications.spondee.measure.Speed
import inkapplications.spondee.structure.DerivedUnit
import inkapplications.spondee.structure.div

object Knots: DerivedUnit<Speed>(
    definition = (MetersPerSecond / 3600) / 1852,
    symbol = "kn"
)

