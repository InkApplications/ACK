package com.inkapplications.ack.parser.extension

import com.inkapplications.ack.parser.chunk.Chunker
import com.inkapplications.ack.parser.chunk.common.CompositeChunker

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

