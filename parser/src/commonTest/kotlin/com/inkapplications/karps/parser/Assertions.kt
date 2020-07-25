package com.inkapplications.karps.parser

import kotlin.math.abs
import kotlin.reflect.KClass
import kotlin.test.assertTrue

fun assertEquals(expected: Double, actual: Double, delta: Double, message: String? = null) {
    val actualDelta = abs(expected - actual)
    assertTrue(actualDelta < delta, message ?: "Expected $actual to be within $delta of $expected but was off by: $actualDelta")
}

inline fun <reified T: Any> assertType(expected: KClass<T>, actual: Any, whenTrue: T.() -> Unit = {}) {
    assertTrue(actual is T, "Expected type <${expected.simpleName}> but was <${actual::class.simpleName}>")
    whenTrue(actual)
}
