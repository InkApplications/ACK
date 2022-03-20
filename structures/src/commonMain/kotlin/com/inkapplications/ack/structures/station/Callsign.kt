package com.inkapplications.ack.structures.station

/**
 * Wraps a canonical callsign value.
 */
class Callsign(value: String) {
    /**
     * The canonical representation of the callsign.
     *
     * This value is expressed as an uppercase string, and can be safely used
     * for comparisons.
     */
    val canonical = value.uppercase()

    override fun toString(): String = canonical
    override fun hashCode(): Int = canonical.hashCode()
    override fun equals(other: Any?): Boolean = when (other) {
        is Callsign -> canonical == other.canonical
        else -> super.equals(other)
    }
}
