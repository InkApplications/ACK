package com.inkapplications.ack.codec

import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData

/**
 * Generate the packet information field from a packet object.
 */
interface PacketDataGenerator {
    /**
     * Generate the information field for [packet] if supported.
     *
     * If the data found in packet is not supported by this serializer, a
     * [UnhandledEncodingException] should be thrown.
     *
     * @param packet The packet data to be encoded
     * @param config Preferences to specify how the data should be encoded.
     */
    fun generate(packet: PacketData, config: EncodingConfig = EncodingConfig()): String
}
