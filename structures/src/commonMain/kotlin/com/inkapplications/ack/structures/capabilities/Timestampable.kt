package com.inkapplications.ack.structures.capabilities

import kotlinx.datetime.Instant

/**
 * Packet with the ability to contain a originating time.
 */
interface Timestampable {
    /**
     * Originating time that the event or packet data was sent or relevant.
     */
    val timestamp: Instant?
}
