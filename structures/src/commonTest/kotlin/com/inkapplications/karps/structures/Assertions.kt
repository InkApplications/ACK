package com.inkapplications.karps.structures

import kotlin.math.abs
import kotlin.test.assertTrue

fun assertEquals(expected: Double, actual: Double, delta: Double, message: String? = null) {
    val actualDelta = abs(expected - actual)
    assertTrue(actualDelta < delta, message ?: "Expected $actual to be within $delta of $expected but was off by: $actualDelta")
}

fun assertEquals(expected: Float, actual: Float, delta: Float, message: String? = null) {
    val actualDelta = abs(expected - actual)
    assertTrue(actualDelta < delta, message ?: "Expected $actual to be within $delta of $expected but was off by: $actualDelta")
}
