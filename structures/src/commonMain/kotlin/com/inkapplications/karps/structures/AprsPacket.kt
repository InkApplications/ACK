package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.*

/**
 * A Single APRS record.
 */
sealed class AprsPacket {
    abstract val received: Timestamp
    abstract val dataTypeIdentifier: Char
    abstract val source: Address
    abstract val destination: Address
    abstract val digipeaters: List<Digipeater>

    data class Position(
        override val received: Timestamp,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val coordinates: Coordinates,
        val symbol: Symbol,
        val comment: String,
        val course: Bearing? = null,
        val speed: Speed? = null,
        val timestamp: Timestamp? = null
    ): AprsPacket() {
        val supportsMessaging = when (dataTypeIdentifier) {
            '=', '@' -> true
            else -> false
        }
    }

    data class Weather(
        override val received: Timestamp,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val windData: WindData,
        val precipitation: Precipitation,
        val coordinates: Coordinates? = null,
        val temperature: Temperature? = null,
        val humidity: Percentage? = null,
        val pressure: Pressure? = null,
        val irradiance: Irradiance? = null,
        val timestamp: Timestamp? = null,
        val position: Coordinates? = null,
        val symbol: Symbol? = null
    ): AprsPacket() {
        @Deprecated("APRS traditionally calls this field luminosity, however this is actually measured in irradiance.", ReplaceWith("irradiance"))
        val luminosity = irradiance
    }

    data class Unknown(
        override val received: Timestamp,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val body: String
    ): AprsPacket()
}
