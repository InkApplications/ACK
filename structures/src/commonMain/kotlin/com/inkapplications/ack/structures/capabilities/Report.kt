package com.inkapplications.ack.structures.capabilities

import com.inkapplications.ack.structures.Trajectory
import com.inkapplications.ack.structures.TransmitterInfo
import inkapplications.spondee.measure.Length

/**
 * Packet containing metadata about the transmitter or object.
 */
interface Report: Mapable {
    /**
     * The altitude of the reported item.
     */
    val altitude: Length?

    /**
     * The trajectory of the reported item.
     */
    val trajectory: Trajectory?

    /**
     * The range of the transmitter item.
     */
    val range: Length?

    /**
     * Additional information about the transmitter.
     */
    val transmitterInfo: TransmitterInfo?
}
