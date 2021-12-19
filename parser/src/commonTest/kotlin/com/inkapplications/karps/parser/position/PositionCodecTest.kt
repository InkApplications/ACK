package com.inkapplications.karps.parser.position

import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.Knots
import com.inkapplications.karps.structures.unit.Strength
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.measure.Watts
import inkapplications.spondee.spatial.*
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class PositionCodecTest {
    @Test
    fun encodedDefault() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('/', '>'),
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
            symbol = symbolOf('/', '>'),
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
            symbol = symbolOf('/', '>'),
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
                symbol = symbolOf('/', '>'),
                signalInfo = SignalInfo(
                    strength = Strength(2),
                    direction = Cardinal.South.toAngle(),
                    height = Feet.of(80),
                    gain = Bels.of(Deci, 6),
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
                symbol = symbolOf('/', '>'),
                altitude = Feet.of(10004),
            )
        }
    }

    @Test
    fun illegal() {
        assertFails {
            PositionCodec.encodeBody(
                config = EncodingConfig(),
                coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
                symbol = symbolOf('/', '>'),
                altitude = Feet.of(10004),
                signalInfo = SignalInfo(
                    strength = Strength(2),
                    direction = Cardinal.South.toAngle(),
                    height = Feet.of(80),
                    gain = Bels.of(Deci, 6),
                )
            )
        }
    }

    @Test
    fun encodedAltitude() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('/', '>'),
            altitude = Feet.of(10004),
        )

        assertEquals("/5L!!<*e7>S]Q", result)
    }

    @Test
    fun encodedRange() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('/', '>'),
            range = Miles.of(20),
        )

        assertEquals("/5L!!<*e7>{?Q", result)
    }

    @Test
    fun encodedTrajectory() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('/', '>'),
            trajectory = Trajectory(Degrees.of(88), Knots.of(36.2)),
        )

        assertEquals("/5L!!<*e7>7PQ", result)
    }

    @Test
    fun encodeDirectionReport() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('/', '>'),
            directionReport = DirectionReport(
                trajectory = Degrees.of(88) at Knots.of(36),
                bearing = Degrees.of(270),
                quality = QualityReport(
                    number = 7,
                    range = Miles.of(4),
                    accuracy = Degrees.of(1),
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
            symbol = symbolOf('/', '>'),
            signalInfo = SignalInfo(
                strength = Strength(2),
                direction = Cardinal.South.toAngle(),
                height = Feet.of(80),
                gain = Bels.of(Deci, 6),
            )
        )

        assertEquals("4930.00N/07245.00W>DFS2364", result)
    }

    @Test
    fun encodeTransmitterInfo() {
        val result = PositionCodec.encodeBody(
            config = EncodingConfig(),
            coordinates = GeoCoordinates(49.5.latitude, (-72.75).longitude),
            symbol = symbolOf('/', '>'),
            transmitterInfo = TransmitterInfo(
                power = Watts.of(25),
                height = Feet.of(20),
                gain = Bels.of(Deci, 3),
                direction = Cardinal.East.toAngle(),
            )
        )

        assertEquals("4930.00N/07245.00W>PHG5132", result)
    }
}
