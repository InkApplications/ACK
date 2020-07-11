package com.inkapplications.karps.parser

import kotlin.math.abs
import kotlin.test.assertTrue

fun assertEquals(expected: Double, actual: Double, delta: Double, message: String? = null) {
    val actualDelta = abs(expected - actual)
    assertTrue(actualDelta < delta, message ?: "Expected $actual to be within $delta of $expected but was off by: $actualDelta")
}
