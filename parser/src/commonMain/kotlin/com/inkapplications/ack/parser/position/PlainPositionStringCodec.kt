package com.inkapplications.ack.parser.position

import com.inkapplications.ack.parser.ambiguousValue
import com.inkapplications.ack.parser.chunk.requireControl
import com.inkapplications.ack.parser.format.fixedLength
import inkapplications.spondee.spatial.*
import kotlin.math.roundToInt

internal object PlainPositionStringCodec {
    fun decodeLatitude(data: String): Latitude {
        val latDegrees = data.substring(0, 2).ambiguousValue
        val latMinutes = data.substring(2, 4).ambiguousValue
        data[4].requireControl('.')
        val latSeconds = data.substring(5, 7).ambiguousValue * .6f
        val latCardinal = data[7].toCardinal()

        return latitudeOf(
            degreesComponent = latDegrees,
            minutesComponent = latMinutes,
            secondsComponent = latSeconds,
            cardinal = latCardinal,
        )
    }

    fun encodeLatitude(latitude: Latitude): String {
        val degrees = latitude.degreesComponent.fixedLength(2)
        val minutes = latitude.minutesComponent.fixedLength(2)
        val seconds = latitude.secondsComponent.div(.6).roundToInt().fixedLength(2)
        val cardinal = latitude.cardinal.symbol

        return "$degrees$minutes.$seconds$cardinal"
    }

    fun encodeLongitude(longitude: Longitude): String {
        val degrees = longitude.degreesComponent.fixedLength(3)
        val minutes = longitude.minutesComponent.fixedLength(2)
        val seconds = longitude.secondsComponent.div(.6).roundToInt().fixedLength(2)
        val cardinal = longitude.cardinal.symbol

        return "$degrees$minutes.$seconds$cardinal"
    }

    fun decodeLongitude(data: String): Longitude {
        val longDegrees = data.substring(0, 3).ambiguousValue
        val longMinutes = data.substring(3, 5).ambiguousValue
        data[5].requireControl('.')
        val longSeconds = data.substring(6, 8).ambiguousValue * .6f
        val longCardinal = data[8].toCardinal()

        return longitudeOf(
            degreesComponent = longDegrees,
            minutesComponent = longMinutes,
            secondsComponent = longSeconds,
            cardinal = longCardinal
        )
    }
}

