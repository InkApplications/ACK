package com.inkapplications.karps.structures.unit

import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageTest {
    @Test
    fun conversions() {
        assertEquals(42, 42.percent.intValue)
        assertEquals(42, .42.asPercentage.intValue)
        assertEquals(42, .42f.asPercentage.intValue)
        assertEquals(.42f, 42.percent.fractionalValue, .001f)
    }
}
