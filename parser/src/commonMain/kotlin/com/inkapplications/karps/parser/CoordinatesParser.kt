package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*

internal object CoordinatesParser {
    private val format = Regex("""^([0-9\s]{2})([0-9\s]{2})\.([0-9\s]{2})([NS]).([0-9\s]{3})([0-9\s]{2})\.([0-9\s]{2})([EW]).""")

    fun fromStringBody(body: String): Coordinates {
        val result = format.find(body) ?: throw IllegalArgumentException("Invalid coordinate format: $body")

        val (
            latDegrees,
            latMinutes,
            latSeconds,
            latCardinal,
            longDegrees,
            longMinutes,
            longSeconds,
            longCardinal
        ) = result.destructured

        val latitude = Latitude(
            degrees = latDegrees.value.toInt(),
            minutes = latMinutes.value.toInt(),
            seconds = latSeconds.value * 0.6,
            cardinal = latCardinal.single().toCardinal()
        )
        val longitude = Longitude(
            degrees = longDegrees.value.toInt(),
            minutes = longMinutes.value.toInt(),
            seconds = longSeconds.value * 0.6,
            cardinal = longCardinal.single().toCardinal()
        )

        return Coordinates(latitude, longitude)
    }

    private val String.value: Double get() = replace(' ', '0').takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0
}
