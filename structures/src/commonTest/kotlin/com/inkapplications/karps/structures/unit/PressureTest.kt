package com.inkapplications.karps.structures.unit

import com.inkapplications.karps.structures.assertEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class PressureTest {
    @Test
    fun conversions() {
        assertEquals(123, 123.decapascals.decapascals)
        assertEquals(12, 123.decapascals.millibars)
        assertEquals(0.0123f, 123.decapascals.bars, .0001f)
        assertEquals(1230, 123.decapascals.pascals)

        assertEquals(1230, 123.millibars.decapascals)
        assertEquals(12300, 1.23.bars.decapascals)
        assertEquals(12, 123.pascals.decapascals)
    }
}
