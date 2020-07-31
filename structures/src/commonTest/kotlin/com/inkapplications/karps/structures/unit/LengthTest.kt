package com.inkapplications.karps.structures.unit

import com.inkapplications.karps.structures.assertEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class LengthTest {
    @Test
    fun conversions() {
        val result = Length(123)
        assertEquals(1.23f, result.inches, "Inch Representation")
        assertEquals(3.1242f, result.centimeters, .000001f, "Centemeter Representation")
        assertEquals(31.242f, result.millimeters, .000001f, "Millimeter Representation")

        assertEquals(123, 123.hundredthsOfInch.hundredthsOfInches, "Creation from 100's of inches")
        assertEquals(123, 1.23.inches.hundredthsOfInches, "Creation from inches")
        assertEquals(123, 3.1242.centimeters.hundredthsOfInches, "Creation from centimeters")
        assertEquals(123, 31.242.millimeters.hundredthsOfInches, "Creation from millimeters")
    }
}
