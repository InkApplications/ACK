package com.inkapplications.karps.structures

sealed interface Capability {
    val key: String

    data class Token(override val key: String): Capability
    data class Value(override val key: String, val value: String): Capability
}

fun capabilityOf(key: String, value: String?): Capability {
    return if (value == null) Capability.Token(key) else Capability.Value(key, value)
}
