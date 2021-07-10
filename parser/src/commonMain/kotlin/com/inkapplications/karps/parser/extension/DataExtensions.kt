package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.UnionContainer
import com.inkapplications.karps.structures.DirectionReport
import com.inkapplications.karps.structures.SignalInfo
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.TransmitterInfo
import inkapplications.spondee.measure.Length

internal sealed class DataExtensions<T: Any>: UnionContainer<T> {
    data class TrajectoryExtra(
        override val value: Trajectory
    ): DataExtensions<Trajectory>()

    class RangeExtra(
        override val value: Length
    ): DataExtensions<Length>()

    class TransmitterInfoExtra(
        override val value: TransmitterInfo
    ): DataExtensions<TransmitterInfo>()

    class OmniDfSignalExtra(
        override val value: SignalInfo
    ): DataExtensions<SignalInfo>()

    class DirectionReportExtra(
        override val value: DirectionReport
    ): DataExtensions<DirectionReport>()
}
