package com.inkapplications.ack.structures

sealed interface Capability {
    val key: String

    data class Token(override val key: String): Capability {
        override fun toString(): String = key
    }
    data class Value(override val key: String, val value: String): Capability {
        override fun toString(): String = "$key=$value"
    }
}

fun capabilityOf(key: String, value: String?): Capability {
    return if (value == null) Capability.Token(key) else Capability.Value(key, value)
}
