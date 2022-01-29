package com.inkapplications.ack.parser

import kotlin.reflect.KClass

/**
 * Generic container type, used in lieu of a Union.
 */
internal interface UnionContainer<T: Any> {
    val value: T
}

/**
 * Get the value from the container, assuming it is of a specific type.
 */
internal inline fun <R: Any, reified T: UnionContainer<R>> UnionContainer<*>.valueFor(type: KClass<T>): R? = (this as? T)?.value
