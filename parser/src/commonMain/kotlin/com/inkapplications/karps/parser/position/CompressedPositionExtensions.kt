package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.UnionContainer
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.unit.Distance

/**
 * Union of extras that can appear in the compressed extension data.
 */
sealed class CompressedPositionExtensions<T: Any>: UnionContainer<T> {
    class AltitudeExtra(override val value: Distance): CompressedPositionExtensions<Distance>()
    class TrajectoryExtra(override val value: Trajectory): CompressedPositionExtensions<Trajectory>()
    class RangeExtra(override val value: Distance): CompressedPositionExtensions<Distance>()
}
