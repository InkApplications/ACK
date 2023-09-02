package com.inkapplications.ack.codec.position

import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.unit.Strength
import inkapplications.spondee.measure.metric.watts
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.measure.us.knots
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.*
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.scale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class PositionCodecTest {
    @Test
    fun encodedDefault() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
        )

        assertEquals("/5L!!<*e7>  Q", result)
    }

    @Test
    fun encodedPlainPreferred() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(
                compression = EncodingPreference.Disfavored
            ),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
        )

        assertEquals("4930.00N/07245.00W>", result)
    }

    @Test
    fun encodedPlainRequired() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(
                compression = EncodingPreference.Barred
            ),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
        )

        assertEquals("4930.00N/07245.00W>", result)
    }

    @Test
    fun illegalCompression() {
        assertFails {
            PositionCodec.encodeBody(
                config = EncodingConfig(
                    compression = EncodingPreference.Required
                ),
                coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
                symbol = symbolOf('>', '/'),
                signalInfo = SignalInfo(
                    strength = Strength(2),
                    direction = Cardinal.South.toAngle(),
                    height = 80.feet,
                    gain = 6.scale(Deci).bels,
                )
            )
        }
    }

    @Test
    fun illegalPlain() {
        assertFails {
            PositionCodec.encodeBody(
                config = EncodingConfig(
                    compression = EncodingPreference.Barred
                ),
                coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
                symbol = symbolOf('>', '/'),
                altitude = 10004.feet,
            )
        }
    }

    @Test
    fun illegal() {
        assertFails {
            PositionCodec.encodeBody(
                config = EncodingConfig(),
                coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
                symbol = symbolOf('>', '/'),
                altitude = 10004.feet,
                signalInfo = SignalInfo(
                    strength = Strength(2),
                    direction = Cardinal.South.toAngle(),
                    height = 80.feet,
                    gain = 6.scale(Deci).bels,
                )
            )
        }
    }

    @Test
    fun encodedAltitude() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
            altitude = 10004.feet,
        )

        assertEquals("/5L!!<*e7>S]Q", result)
    }

    @Test
    fun encodedRange() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
            range = 20.miles,
        )

        assertEquals("/5L!!<*e7>{?Q", result)
    }

    @Test
    fun encodedTrajectory() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
            trajectory = Trajectory(88.degrees, 36.2.knots),
        )

        assertEquals("/5L!!<*e7>7PQ", result)
    }

    @Test
    fun encodeDirectionReport() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
            directionReport = DirectionReport(
                trajectory = 88.degrees at 36.knots,
                bearing = 270.degrees,
                quality = QualityReport(
                    number = 7,
                    range = 4.miles,
                    accuracy = 1.degrees,
                )
            )
        )

        assertEquals("4930.00N/07245.00W>088/036/270/729", result)
    }

    @Test
    fun encodeSignalInfo() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
            signalInfo = SignalInfo(
                strength = Strength(2),
                direction = Cardinal.South.toAngle(),
                height = 80.feet,
                gain = 6.scale(Deci).bels,
            )
        )

        assertEquals("4930.00N/07245.00W>DFS2364", result)
    }

    @Test
    fun encodeTransmitterInfo() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('>', '/'),
            transmitterInfo = TransmitterInfo(
                power = 25.watts,
                height = 20.feet,
                gain = 3.scale(Deci).bels,
                direction = Cardinal.East.toAngle(),
            )
        )

        assertEquals("4930.00N/07245.00W>PHG5132", result)
    }
}
