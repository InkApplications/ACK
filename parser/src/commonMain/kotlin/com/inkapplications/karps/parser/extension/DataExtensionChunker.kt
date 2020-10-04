package com.inkapplications.karps.parser.extension

import com.inkapplications.karps.parser.chunk.Chunker
import com.inkapplications.karps.parser.chunk.common.CompositeChunker

/**
 * Composite of all standard Data Extensions.
 */
object DataExtensionChunker: Chunker<DataExtensions<*>> by CompositeChunker(
    DirectionReportExtensionChunker,
    TrajectoryExtensionChunker,
    TransmitterInfoExtensionChunker,
    RangeExtensionChunker,
    SignalExtensionChunker
)

