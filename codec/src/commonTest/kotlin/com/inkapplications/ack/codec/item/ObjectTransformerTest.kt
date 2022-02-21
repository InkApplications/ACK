package com.inkapplications.ack.codec.item

import com.inkapplications.ack.codec.TestData
import com.inkapplications.ack.codec.assertEquals
import com.inkapplications.ack.codec.timestamp.withUtcValues
import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.unit.Knots
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Watts
import inkapplications.spondee.spatial.*
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.*

class ObjectTransformerTest {
    private val transformer = ObjectTransformer(
        timestamps = TestData.timestampModule
    )

    @Test
    fun liveObject() {
        val given = ";LEADER   *092345z4903.50N/07201.75W>088/036"

        val result = transformer.parse(given)
        val resultDateTime = result.timestamp?.toLocalDateTime(TimeZone.UTC)

        assertEquals("LEADER", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals(9, resultDateTime?.dayOfMonth)
        assertEquals(23, resultDateTime?.hour)
        assertEquals(45, resultDateTime?.minute)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(Degrees.of(88) at Knots.of(36), result.trajectory)
    }

    @Test
    fun killedObject() {
        val given = ";LEADER   _092345z4903.50N/07201.75W>088/036"

        val result = transformer.parse(given)
        val resultDateTime = result.timestamp?.toLocalDateTime(TimeZone.UTC)

        assertEquals("LEADER", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals(9, resultDateTime?.dayOfMonth)
        assertEquals(23, resultDateTime?.hour)
        assertEquals(45, resultDateTime?.minute)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(Degrees.of(88) at Knots.of(36), result.trajectory)
    }

    @Test
    fun nonObject() {
        val given = ";LEA_092345z4903.50N/07201.75W>088/036"

        assertFails { transformer.parse(given) }
    }

    @Test
    fun liveGenerated() {
        val given = PacketData.ObjectReport(
            timestamp = TestData.now.withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
            ),
            name = "LEADER",
            state = ReportState.Live,
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0292).longitude),
            symbol = symbolOf('/', '>'),
            comment = "Hello World",
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = TransmitterInfo(
                power = Watts.of(25),
                height = Feet.of(20),
                gain = Bels.of(Deci, 3),
                direction = Cardinal.East.toAngle(),
            ),
            signalInfo = null,
            directionReport = null,
        )

        val result = transformer.generate(given)

        assertEquals(";LEADER   *092345z4903.50N/07201.75W>PHG5132", result)
    }
}
