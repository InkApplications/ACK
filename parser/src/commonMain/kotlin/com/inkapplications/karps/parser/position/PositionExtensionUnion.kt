package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.unit.Distance
import com.inkapplications.karps.structures.Trajectory as TrajectoryValue

/**
 * Encoded data extension field.
 *
 * Both compressed and plain positions may contain data extensions for extra
 * information, but may only contain one of the following types.
 */
internal sealed class PositionExtensionUnion {
    data class Altitude(
        val value: Distance?
    ): PositionExtensionUnion()

    data class Trajectory(
        val value: TrajectoryValue?
    ): PositionExtensionUnion()

    data class Range(
        val value: Distance?
    ): PositionExtensionUnion()
}
