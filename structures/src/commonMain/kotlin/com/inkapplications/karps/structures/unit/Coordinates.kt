package com.inkapplications.karps.structures.unit

/**
 * Geo-positioning coordinates.
 */
data class Coordinates(val latitude: Latitude, val longitude: Longitude) {
    override fun toString() = "$latitude $longitude"
}
