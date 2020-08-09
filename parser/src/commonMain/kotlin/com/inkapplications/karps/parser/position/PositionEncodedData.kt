package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.unit.Coordinates

/**
 * Represents The information that can be included in the position encoding.
 *
 * A compressed position always includes data for certain extras besides the
 * coordinates. This wraps all of the encoded data so that it may be parsed
 * all at once.
 */
internal class PositionEncodedData(
    val coordinates: Coordinates,
    val symbol: Symbol,
    val extension: PositionExtensionUnion?
)
