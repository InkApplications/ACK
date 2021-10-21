package com.inkapplications.karps.structures.capabilities

/**
 * Packet that contains an arbitrary comment.
 */
interface Commented {
    /**
     * Arbitrary comment or data left over after parsing.
     *
     * Note: This can sometimes contain nonstandard data about the station.
     */
    val comment: String
}
