package com.inkapplications.ack.structures

import com.inkapplications.ack.structures.capabilities.*
import inkapplications.spondee.measure.Irradiance
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Pressure
import inkapplications.spondee.measure.Temperature
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.Instant

sealed class PacketData {
    data class Position(
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val timestamp: Instant? = null,
        override val altitude: Length? = null,
        override val trajectory: Trajectory? = null,
        override val range: Length? = null,
        override val transmitterInfo: TransmitterInfo? = null,
        val signalInfo: SignalInfo? = null,
        val directionReportExtra: DirectionReport? = null,
        val supportsMessaging: Boolean = false,
    ): PacketData(), Report, Commented, Timestampable

    data class Weather(
        override val timestamp: Instant? = null,
        override val coordinates: GeoCoordinates? = null,
        override val symbol: Symbol? = null,
        val windData: WindData = WindData(),
        val precipitation: Precipitation = Precipitation(),
        val temperature: Temperature? = null,
        val humidity: Percentage? = null,
        val pressure: Pressure? = null,
        val irradiance: Irradiance? = null,
    ): PacketData(), Timestampable, Mapable {
        @Deprecated("APRS traditionally calls this field luminosity, however this is actually measured in irradiance.", ReplaceWith("irradiance"), level = DeprecationLevel.ERROR)
        val luminosity = irradiance
    }

    data class ObjectReport(
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val name: String,
        val state: ReportState,
        override val timestamp: Instant? = null,
        override val altitude: Length? = null,
        override val trajectory: Trajectory? = null,
        override val range: Length? = null,
        override val transmitterInfo: TransmitterInfo? = null,
        val signalInfo: SignalInfo? = null,
        val directionReport: DirectionReport? = null,
    ): PacketData(), Named, Report, Commented, Timestampable

    data class ItemReport(
        override val name: String,
        val state: ReportState,
        override val coordinates: GeoCoordinates,
        override val symbol: Symbol,
        override val comment: String,
        override val altitude: Length? = null,
        override val trajectory: Trajectory? = null,
        override val range: Length? = null,
        override val transmitterInfo: TransmitterInfo? = null,
        val signalInfo: SignalInfo? = null,
        val directionReport: DirectionReport? = null,
    ): PacketData(), Named, Report, Commented

    data class Message(
        val addressee: Address,
        val message: String,
        val messageNumber: Int? = null,
    ): PacketData()

    data class TelemetryReport(
        val sequenceId: String,
        val data: TelemetryValues,
        override val comment: String,
    ): PacketData(), Commented

    data class StatusReport(
        val status: String,
        override val timestamp: Instant? = null,
    ): PacketData(), Timestampable

    data class CapabilityReport(
        val capabilityData: Set<Capability>,
    ): PacketData()

    data class Unknown(
        val body: String,
    ): PacketData()
}
