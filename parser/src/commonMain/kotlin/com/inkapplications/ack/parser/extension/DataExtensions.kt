package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.UnionContainer
import com.inkapplications.ack.structures.DirectionReport
import com.inkapplications.ack.structures.SignalInfo
import com.inkapplications.ack.structures.Trajectory
import com.inkapplications.ack.structures.TransmitterInfo
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
