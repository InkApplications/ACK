package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.capabilities.*
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
    abstract val received: Instant
    abstract val dataTypeIdentifier: Char
    abstract val source: Address
    abstract val destination: Address
    abstract val digipeaters: List<Digipeater>
    abstract val raw: ByteArray

    data class Position(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        override val timestamp: Instant?,
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val altitude: Length?,
        override val trajectory: Trajectory?,
        override val range: Length?,
        override val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?
    ): AprsPacket(), Report, Commented, Timestamped {
        val supportsMessaging = when (dataTypeIdentifier) {
            '=', '@' -> true
            else -> false
        }
    }

    data class Weather(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        override val timestamp: Instant?,
        val windData: WindData,
        val precipitation: Precipitation,
        override val coordinates: GeoCoordinates?,
        override val symbol: Symbol?,
        val temperature: Temperature?,
        val humidity: Percentage?,
        val pressure: Pressure?,
        val irradiance: Irradiance?
    ): AprsPacket(), Timestamped, Mapable {
        @Deprecated("APRS traditionally calls this field luminosity, however this is actually measured in irradiance.", ReplaceWith("irradiance"), level = DeprecationLevel.ERROR)
        val luminosity = irradiance
    }

    data class ObjectReport(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        override val name: String,
        val state: ReportState,
        override val timestamp: Instant?,
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val altitude: Length?,
        override val trajectory: Trajectory?,
        override val range: Length?,
        override val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?
    ): AprsPacket(), Named, Report, Commented, Timestamped

    data class ItemReport(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        override val name: String,
        val state: ReportState,
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val altitude: Length?,
        override val trajectory: Trajectory?,
        override val range: Length?,
        override val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?
    ): AprsPacket(), Named, Report, Commented

    data class Message(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        val addressee: Address,
        val message: String,
        val messageNumber: Int?
    ): AprsPacket()

    data class TelemetryReport(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        val sequenceId: String,
        val data: TelemetryValues,
        override val comment: String,
    ): AprsPacket(), Commented

    data class StatusReport(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        override val timestamp: Instant?,
        val status: String,
    ): AprsPacket(), Timestamped

    data class CapabilityReport(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        val capabilityData: Set<Capability>
    ): AprsPacket()

    data class Unknown(
        override val received: Instant,
        override val dataTypeIdentifier: Char,
        override val source: Address,
        override val destination: Address,
        override val digipeaters: List<Digipeater>,
        override val raw: ByteArray,
        val body: String
    ): AprsPacket()
}
