package com.inkapplications.ack.codec.position

import com.inkapplications.ack.codec.TestData
import com.inkapplications.ack.codec.assertEquals
import com.inkapplications.ack.codec.timestamp.withUtcValues
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.EncodingPreference
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.symbolOf
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.measure.us.toMiles
import inkapplications.spondee.spatial.*
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.toDouble
import inkapplications.spondee.structure.value
import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.test.assertEquals

class PositionParserTest {
    private val parser = PositionTransformer(TestData.timestampModule)

    @Test
    fun plainPosition() {
        val given = "!4903.50N/07201.75W-Test 001234"

        val result = parser.parse(given)

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 1e-4)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 1e-4)
        assertEquals("Test 001234", result.comment)
        assertEquals(symbolOf('-', '/'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertNull(result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun plainTransmitterInfoExtension() {
        val given = "=4903.50N/07201.75W#PHG5132Test 001234"

        val result = parser.parse(given)

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 1e-4)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 1e-4)
        assertEquals("Test 001234", result.comment)
        assertEquals(symbolOf('#', '/'), result.symbol)
        assertEquals(25.0, result.transmitterInfo?.power?.toWatts()!!.toDouble(), 1e-15)
        assertEquals(20.0, result.transmitterInfo?.height?.toFeet()!!.toDouble(), 1e-15)
        assertEquals(3.0, result.transmitterInfo?.gain?.toBels()?.value(Deci)!!.toDouble(), 1e-15)
        assertTrue(result.supportsMessaging)
        assertNull(result.timestamp)
        assertEquals(Cardinal.East.toAngle().toDegrees().toDouble(), result.transmitterInfo?.direction?.toDegrees()!!.toDouble(), 1e-15)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun plainAltitude() {
        val given = "!4903.50N/07201.75W-Test/A=001234"

        val result = parser.parse(given)

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 1e-4)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 1e-4)
        assertEquals("Test", result.comment)
        assertEquals(symbolOf('-', '/'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertNull(result.timestamp)
        assertEquals(1234.0, result.altitude?.toFeet()!!.toDouble())
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun plainTimestamp() {
        val given = "/092345z4903.50N/07201.75W>Test1234"

        val result = parser.parse(given)

        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 1e-4)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 1e-4)
        assertEquals("Test1234", result.comment)
        assertEquals(symbolOf('>', '/'), result.symbol)
        assertEquals(expected, result.timestamp)
        assertFalse(result.supportsMessaging)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)

    }

    @Test
    fun compressedPosition() {
        val given = "@/5L!!<*e7> sTComment"

        val result = parser.parse(given)

        assertEquals(49.5, result.coordinates.latitude.asDecimal, 1e-1)
        assertEquals(-72.75, result.coordinates.longitude.asDecimal, 1e-2)
        assertEquals("Comment", result.comment)
        assertEquals(symbolOf('>', '/'), result.symbol)
        assertTrue(result.supportsMessaging)
        assertNull(result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun compressedRange() {
        val given = "//5L!!<*e7>{?!Comment"

        val result = parser.parse(given)

        assertEquals(49.5, result.coordinates.latitude.asDecimal, 1e-1)
        assertEquals(-72.75, result.coordinates.longitude.asDecimal, 1e-2)
        assertEquals("Comment", result.comment)
        assertEquals(symbolOf('>', '/'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertNull(result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertEquals(20.1253137781, result.range?.toMiles()?.toDouble(), 1e-10)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun compressedTimestamp() {
        val given = "/092345z/5L!!<*e7>{?!Comment"

        val result = parser.parse(given)

        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        assertEquals(49.5, result.coordinates.latitude.asDecimal, 1e-1)
        assertEquals(-72.75, result.coordinates.longitude.asDecimal, 1e-2)
        assertEquals("Comment", result.comment)
        assertEquals(symbolOf('>', '/'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertEquals(expected, result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertEquals(20.1253137781, result.range?.toMiles()?.toDouble(), 1e-10)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun nonPosition() {
        val given = ":Hello World"

        assertFails { parser.parse(given) }
    }

    @Test
    fun generatePlainMinimum() {
        val given = PacketData.Position(
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('-', '/'),
            comment = "Test 01234",
            timestamp = null,
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = null,
            signalInfo = null,
            directionReportExtra = null,
            supportsMessaging = false,
        )

        val result = parser.generate(given, EncodingConfig(compression = EncodingPreference.Disfavored))

        assertEquals("!4903.50N/07201.75W-Test 01234", result)
    }

    @Test
    fun generateWithMessaging() {
        val given = PacketData.Position(
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('-', '/'),
            comment = "Test 01234",
            timestamp = null,
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = null,
            signalInfo = null,
            directionReportExtra = null,
            supportsMessaging = true,
        )

        val result = parser.generate(given, EncodingConfig(compression = EncodingPreference.Disfavored))

        assertEquals("=4903.50N/07201.75W-Test 01234", result)
    }

    @Test
    fun generateWithTimestamp() {
        val t = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )
        val given = PacketData.Position(
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('-', '/'),
            comment = "Test 01234",
            timestamp = Clock.System.now().withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            ),
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = null,
            signalInfo = null,
            directionReportExtra = null,
            supportsMessaging = false,
        )

        val result = parser.generate(given, EncodingConfig(compression = EncodingPreference.Disfavored))

        assertEquals("/092345z4903.50N/07201.75W-Test 01234", result)
    }

    @Test
    fun generateWithMessagingAndTimestamp() {
        val t = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )
        val given = PacketData.Position(
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0291).longitude),
            symbol = symbolOf('-', '/'),
            comment = "Test 01234",
            timestamp = Clock.System.now().withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            ),
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = null,
            signalInfo = null,
            directionReportExtra = null,
            supportsMessaging = true,
        )

        val result = parser.generate(given, EncodingConfig(compression = EncodingPreference.Disfavored))

        assertEquals("@092345z4903.50N/07201.75W-Test 01234", result)
    }
}
