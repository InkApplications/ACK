package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.unit.Distance
import com.inkapplications.karps.structures.unit.Trajectory

/**
 * Encoded data extra field.
 *
 * An encoded position contains two bytes to indicate either the
 * altitude, course, or pre-calculated range of the station.
 * Since it can only contain one of these fields, but not multiple, this
 * sealed class acts like a union to represent which field was sent in the
 * encoded data.
 */
internal sealed class PositionExtraUnion {
    abstract val compressionInfo: CompressionInfo?

    data class Altitude(
        override val compressionInfo: CompressionInfo?,
        val value: Distance?
    ): PositionExtraUnion()

    data class Course(
        override val compressionInfo: CompressionInfo?,
        val value: Trajectory?
    ): PositionExtraUnion()

    data class Range(
        override val compressionInfo: CompressionInfo?,
        val value: Distance?
    ): PositionExtraUnion()
}
