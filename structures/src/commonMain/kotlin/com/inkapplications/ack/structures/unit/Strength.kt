package com.inkapplications.ack.structures.unit

/**
 * S-Meter strength measurement.
 */
@JvmInline
value class Strength(val value: Short) {
    override fun toString(): String = "S${value}"
}
