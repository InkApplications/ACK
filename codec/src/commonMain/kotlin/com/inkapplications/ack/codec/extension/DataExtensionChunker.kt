package com.inkapplications.ack.codec.extension

import com.inkapplications.ack.codec.chunk.Chunker
import com.inkapplications.ack.codec.chunk.common.CompositeChunker

/**
 * Composite of all standard Data Extensions.
 */
internal object DataExtensionChunker: Chunker<DataExtensions<*>> by CompositeChunker(
    DirectionReportExtensionChunker,
    TrajectoryExtensionChunker,
    TransmitterInfoExtensionChunker,
    RangeExtensionChunker,
    SignalExtensionChunker
)

