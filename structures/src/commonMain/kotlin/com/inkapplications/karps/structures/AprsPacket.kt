package com.inkapplications.karps.structures

import com.soywiz.klock.DateTime

/**
 * A Single APRS record.
 */
sealed class AprsPacket {
    abstract val received: DateTime
    abstract val dataTypeIdentifier: Char
    abstract val source: Address
    abstract val destination: Address
    abstract val digipeaters: List<Address>

    data class Position(
        override val received: DateTime,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Address>
    ): AprsPacket()

    data class Unknown(
        override val received: DateTime,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Address>
    ): AprsPacket()
}

