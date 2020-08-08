package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.unit.Distance
import com.inkapplications.karps.structures.unit.Trajectory

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
