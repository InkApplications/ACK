package com.inkapplications.karps.parser

import kotlin.reflect.KClass

/**
 * Generic container type, used in lieu of a Union.
 */
interface UnionContainer<T: Any> {
    val value: T
}

/**
 * Get the value from the container, assuming it is of a specific type.
 */
inline fun <R: Any, reified T: UnionContainer<R>> UnionContainer<*>.valueFor(type: KClass<T>): R? = (this as? T)?.value
