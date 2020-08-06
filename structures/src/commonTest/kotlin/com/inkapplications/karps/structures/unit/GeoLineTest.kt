package com.inkapplications.karps.structures.unit

import kotlin.test.Test
import kotlin.test.assertEquals

class GeoLineTest {
    @Test
    fun fromDecimal() {
        val latitude = Latitude(37.778667)
        val longitude = Longitude(-84.036500)

        assertEquals(37, latitude.degrees)
        assertEquals(46, latitude.minutes)
        com.inkapplications.karps.structures.assertEquals(43.2012f, latitude.seconds, .01f)
        assertEquals(Cardinal.North, latitude.cardinal)

        assertEquals(84, longitude.degrees)
        assertEquals(2, longitude.minutes)
        com.inkapplications.karps.structures.assertEquals(11.4f, longitude.seconds, .01f)
        assertEquals(Cardinal.West, longitude.cardinal)
    }

    @Test
    fun fromDegrees() {
        val latitude = Latitude(37, 46, 43.2012f, Cardinal.North)
        val longitude = Longitude(84, 2, 11.4f, Cardinal.West)

        com.inkapplications.karps.structures.assertEquals(37.778667, latitude.decimal, .0001)
        com.inkapplications.karps.structures.assertEquals(-84.0365, longitude.decimal, .0001)
    }
}
