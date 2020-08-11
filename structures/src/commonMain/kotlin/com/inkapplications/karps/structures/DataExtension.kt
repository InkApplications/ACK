package com.inkapplications.karps.structures

import com.inkapplications.karps.structures.unit.Distance

sealed class DataExtension {
    data class AltitudeExtra(
        val value: Distance?
    ): DataExtension()

    data class TrajectoryExtra(
        val value: Trajectory?
    ): DataExtension()

    data class RangeExtra(
        val value: Distance?
    ): DataExtension()

    data class TransmitterInfoExtra(
        val value: TransmitterInfo
    ): DataExtension()
}
