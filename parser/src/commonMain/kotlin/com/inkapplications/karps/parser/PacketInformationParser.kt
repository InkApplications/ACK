package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*

/**
 * Parse an unknown packet into a known type based on the information field.
 */
interface PacketInformationParser {
    val dataTypeFilter: CharArray? get() = null
    fun parse(data: PacketInformation): PacketInformation
}

data class PacketInformation(
    val dataType: Char,
    val body: String,
    val timestamp: Timestamp? = null,
    val position: Coordinates? = null,
    val symbol: Symbol? = null,
    val extension: DataExtension? = null,
    val windData: WindData? = null,
    val precipitation: Precipitation? = null,
    val temperature: Temperature? = null,
    val humidity: Percentage? = null,
    val pressure: Pressure? = null,
    val irradiance: Irradiance? = null
)
