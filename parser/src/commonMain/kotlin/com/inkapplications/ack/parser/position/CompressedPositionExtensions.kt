package com.inkapplications.ack.parser.position

import com.inkapplications.ack.parser.UnionContainer
import com.inkapplications.ack.structures.Trajectory
import inkapplications.spondee.measure.Length

/**
 * Union of extras that can appear in the compressed extension data.
 */
internal sealed class CompressedPositionExtensions<T: Any>: UnionContainer<T> {
    class AltitudeExtra(override val value: Length): CompressedPositionExtensions<Length>()
    class TrajectoryExtra(override val value: Trajectory): CompressedPositionExtensions<Trajectory>()
    class RangeExtra(override val value: Length): CompressedPositionExtensions<Length>()
}
