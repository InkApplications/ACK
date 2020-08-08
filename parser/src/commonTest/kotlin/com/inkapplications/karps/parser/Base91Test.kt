package com.inkapplications.karps.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class Base91Test {
    @Test
    fun decode() {
        assertEquals(12345678, Base91.decode("1Cmi"))
    }

    @Test
    fun encode() {
        assertEquals("1Cmi", Base91.encode(12345678))
    }
}
