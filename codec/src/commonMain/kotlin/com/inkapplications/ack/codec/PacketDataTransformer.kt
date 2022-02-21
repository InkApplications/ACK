package com.inkapplications.ack.codec

/**
 * Structure with the capability to both parse and generate packet data.
 */
interface PacketDataTransformer: PacketDataParser, PacketDataGenerator
