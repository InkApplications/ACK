package com.inkapplications.karps.structures.capabilities

import com.inkapplications.karps.structures.Symbol
import inkapplications.spondee.spatial.GeoCoordinates

/**
 * Packet that can be displayed on a Map
 */
interface Mapable {
    /**
     * Lat/Lng position of the item to be displayed.
     */
    val coordinates: GeoCoordinates?

    /**
     * A visual symbol to be displayed for the item.
     */
    val symbol: Symbol?
}
