package com.inkapplications.karps.structures

import inkapplications.spondee.measure.Irradiance
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Pressure
import inkapplications.spondee.measure.Temperature
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.Instant

/**
 * A Single APRS record.
 */
sealed class AprsPacket {
    abstract val raw: String
    abstract val received: Instant
    abstract val dataTypeIdentifier: Char
    abstract val source: Address
    abstract val destination: Address
    abstract val digipeaters: List<Digipeater>

    data class Position(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val timestamp: Instant?,
        val coordinates: GeoCoordinates,
        val symbol: Symbol,
        val comment: String,
        val altitude: Length?,
        val trajectory: Trajectory?,
        val range: Length?,
        val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?
    ): AprsPacket() {
        val supportsMessaging = when (dataTypeIdentifier) {
            '=', '@' -> true
            else -> false
        }
    }

    data class Weather(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val timestamp: Instant?,
        val windData: WindData,
        val precipitation: Precipitation,
        val coordinates: GeoCoordinates?,
        val symbol: Symbol?,
        val temperature: Temperature?,
        val humidity: Percentage?,
        val pressure: Pressure?,
        val irradiance: Irradiance?
    ): AprsPacket() {
        @Deprecated("APRS traditionally calls this field luminosity, however this is actually measured in irradiance.", ReplaceWith("irradiance"), level = DeprecationLevel.ERROR)
        val luminosity = irradiance
    }

    data class ObjectReport(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val name: String,
        val state: ReportState,
        val timestamp: Instant?,
        val coordinates: GeoCoordinates,
        val symbol: Symbol,
        val comment: String,
        val altitude: Length?,
        val trajectory: Trajectory?,
        val range: Length?,
        val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?
    ): AprsPacket()

    data class ItemReport(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val name: String,
        val state: ReportState,
        val coordinates: GeoCoordinates,
        val symbol: Symbol,
        val comment: String,
        val altitude: Length?,
        val trajectory: Trajectory?,
        val range: Length?,
        val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?
    ): AprsPacket()

    data class Message(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val addressee: Address,
        val message: String,
        val messageNumber: Int?
    ): AprsPacket()

    data class TelemetryReport(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val sequenceId: String,
        val data: TelemetryValues,
        val comment: String,
    ): AprsPacket()

    data class StatusReport(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val time: Instant?,
        val status: String,
    ): AprsPacket()

    data class CapabilityReport(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val capabilityData: Set<Capability>
    ): AprsPacket()

    data class Unknown(
        override val raw: String,
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        val body: String
    ): AprsPacket()
}
