package com.inkapplications.ack.structures.capabilities

/**
 * Packet that identifies something other than itself by name.
 */
interface Named {
    /**
     * Readable name of something other than the station.
     */
    val name: String
}
