package com.inkapplications.karps.structures.unit

import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude

/**
 * Geo-positioning coordinates.
 */
data class Coordinates(val latitude: Latitude, val longitude: Longitude) {
    override fun toString(): String {
        return "$latitude $longitude"
    }
}
