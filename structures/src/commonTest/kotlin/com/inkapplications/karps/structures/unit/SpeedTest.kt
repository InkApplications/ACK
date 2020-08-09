package com.inkapplications.karps.structures.unit

import com.inkapplications.karps.structures.assertEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class SpeedTest {
    @Test
    fun conversions() {
        assertEquals(12, 12.mph.milesPerHour)
        assertEquals(19, 12.mph.kilometersPerHour)
        assertEquals(10.4277f, 12.mph.knots, 0.00001f)
        assertEquals(14, 12.knots.milesPerHour)
        assertEquals(7, 12.kph.milesPerHour)
    }
}
