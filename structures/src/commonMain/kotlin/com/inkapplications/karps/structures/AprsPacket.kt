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
    abstract val body: String
    abstract val extension: DataExtension?
    abstract val timestamp: Timestamp?

    abstract fun withBody(body: String): AprsPacket
    abstract fun withExtension(extension: DataExtension?): AprsPacket
    abstract fun withTimestamp(timestamp: Timestamp?): AprsPacket

    data class Position(
        override val received: Timestamp,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val body: String,
        val coordinates: Coordinates,
        val symbol: Symbol,
        override val extension: DataExtension? = null,
        override val timestamp: Timestamp? = null
    ): AprsPacket() {
        val supportsMessaging = when (dataTypeIdentifier) {
            '=', '@' -> true
            else -> false
        }
        val comment = body

        override fun withBody(body: String): AprsPacket = copy(body = body)
        override fun withExtension(extension: DataExtension?): AprsPacket = copy(extension = extension)
        override fun withTimestamp(timestamp: Timestamp?): AprsPacket = copy(timestamp = timestamp)
    }

    data class Weather(
        override val received: Timestamp,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val body: String,
        val windData: WindData,
        val precipitation: Precipitation,
        override val timestamp: Timestamp? = null,
        val position: Coordinates? = null,
        val symbol: Symbol? = null,
        val temperature: Temperature? = null,
        val humidity: Percentage? = null,
        val pressure: Pressure? = null,
        val irradiance: Irradiance? = null,
        override val extension: DataExtension? = null
    ): AprsPacket() {
        @Deprecated("APRS traditionally calls this field luminosity, however this is actually measured in irradiance.", ReplaceWith("irradiance"))
        val luminosity = irradiance

        override fun withBody(body: String): AprsPacket = copy(body = body)
        override fun withExtension(extension: DataExtension?): AprsPacket = copy(extension = extension)
        override fun withTimestamp(timestamp: Timestamp?): AprsPacket = copy(timestamp = timestamp)
    }

    data class Unknown(
        override val received: Timestamp,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val body: String,
        override val extension: DataExtension? = null,
        override val timestamp: Timestamp? = null
    ): AprsPacket() {
        override fun withBody(body: String): AprsPacket = copy(body = body)
        override fun withExtension(extension: DataExtension?): AprsPacket = copy(extension = extension)
        override fun withTimestamp(timestamp: Timestamp?): AprsPacket = copy(timestamp = timestamp)
    }
}
