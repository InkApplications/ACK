package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.capabilities.*
import inkapplications.spondee.measure.Irradiance
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Pressure
import inkapplications.spondee.measure.Temperature
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.Instant

sealed class PacketData {
    data class Position(
        override val timestamp: Instant?,
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val altitude: Length?,
        override val trajectory: Trajectory?,
        override val range: Length?,
        override val transmitterInfo: TransmitterInfo?,
        val signalInfo: SignalInfo?,
        val directionReportExtra: DirectionReport?,
        val supportsMessaging: Boolean,
    ): PacketData(), Report, Commented, Timestamped

    data class Weather(
        override val timestamp: Instant?,
        val windData: WindData,
        val precipitation: Precipitation,
        override val coordinates: GeoCoordinates?,
        override val symbol: Symbol?,
        val temperature: Temperature?,
        val humidity: Percentage?,
        val pressure: Pressure?,
        val irradiance: Irradiance?
    ): PacketData(), Timestamped, Mapable {
        @Deprecated("APRS traditionally calls this field luminosity, however this is actually measured in irradiance.", ReplaceWith("irradiance"), level = DeprecationLevel.ERROR)
        val luminosity = irradiance
    }

    data class ObjectReport(
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
        val directionReport: DirectionReport?
    ): PacketData(), Named, Report, Commented, Timestamped

    data class ItemReport(
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
        val directionReport: DirectionReport?
    ): PacketData(), Named, Report, Commented

    data class Message(
        val addressee: Address,
        val message: String,
        val messageNumber: Int?
    ): PacketData()

    data class TelemetryReport(
        val sequenceId: String,
        val data: TelemetryValues,
        override val comment: String,
    ): PacketData(), Commented

    data class StatusReport(
        override val timestamp: Instant?,
        val status: String,
    ): PacketData(), Timestamped

    data class CapabilityReport(
        val capabilityData: Set<Capability>
    ): PacketData()

    data class Unknown(
        val body: String
    ): PacketData()
}
