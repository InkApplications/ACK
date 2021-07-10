package com.inkapplications.karps.cli.parse

import com.inkapplications.karps.structures.AprsPacket

sealed interface LineStatus {
    val line: String
    data class Todo(override val line: String): LineStatus
    data class Passed(override val line: String, val packet: AprsPacket): LineStatus
    data class Failed(override val line: String, val error: Throwable): LineStatus
}
