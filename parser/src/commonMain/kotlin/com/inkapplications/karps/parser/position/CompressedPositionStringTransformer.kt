package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.format.Base91
import inkapplications.spondee.spatial.Latitude
import inkapplications.spondee.spatial.Longitude
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude

private const val LAT_RATIO = 380926.0
private const val LAT_OFFSET = 90
private const val LON_RATIO = 190463.0
private const val LON_OFFSET = -180

internal object CompressedPositionStringTransformer {
    fun encodeLatitude(latitude: Latitude): String {
        return latitude.asDecimal
            .let { LAT_OFFSET - it }
            .let { it * LAT_RATIO }
            .toInt()
            .let(Base91::encode)
    }

    fun decodeLatitude(encoded: String): Latitude {
        return encoded.let(Base91::decode)
            .let { it / LAT_RATIO }
            .let { LAT_OFFSET - it }
            .latitude
    }

    fun encodeLongitude(longitude: Longitude): String {
        return longitude.asDecimal
            .let { -LON_OFFSET + it }
            .let { it * LON_RATIO }
            .toInt()
            .let(Base91::encode)
    }

    fun decodeLongitude(encoded: String): Longitude {
        return encoded.let(Base91::decode)
            .let { it / LON_RATIO }
            .let { LON_OFFSET + it }
            .longitude
    }
}
