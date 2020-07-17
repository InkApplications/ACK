package com.inkapplications.karps.structures

/**
 * Geo-positioning coordinates.
 */
data class Coordinates(val latitude: Latitude, val longitude: Longitude) {
    override fun toString(): String {
        return "$latitude $longitude"
    }
}
