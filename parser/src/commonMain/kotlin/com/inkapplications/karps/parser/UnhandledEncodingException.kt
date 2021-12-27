package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.PacketData
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Exception thrown when a Packet cannot be handled by the generator.
 */
class UnhandledEncodingException: IllegalArgumentException()

/**
 * Require that this packet is of specific type or throws an [UnhandledEncodingException]
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified T: PacketData> PacketData.requireType() {
    contract {
        returns() implies (this@requireType is T)
    }
    if (this !is T) throw UnhandledEncodingException()
}
