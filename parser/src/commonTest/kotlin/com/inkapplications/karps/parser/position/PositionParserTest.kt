package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.parser.timestamp.withUtcValues
import com.inkapplications.karps.structures.symbolOf
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.measure.Watts
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import inkapplications.spondee.structure.value
import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.test.assertEquals

class PositionParserTest {
    private val parser = PositionParser(TestData.timestampModule)

    @Test
    fun plainPosition() {
        val given = "!4903.50N/07201.75W-Test 001234"

        val result = parser.parse(given)

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 1e-4)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 1e-4)
        assertEquals("Test 001234", result.comment)
        assertEquals(symbolOf('/', '-'), result.symbol)
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
        assertEquals(symbolOf('/', '#'), result.symbol)
        assertEquals(Watts.of(25), result.transmitterInfo?.power)
        assertEquals(Feet.of(20), result.transmitterInfo?.height)
        assertEquals(Bels.of(Deci, 3), result.transmitterInfo?.gain)
        assertTrue(result.supportsMessaging)
        assertNull(result.timestamp)
        assertEquals(Cardinal.East.toAngle(), result.transmitterInfo?.direction)
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
        assertEquals(symbolOf('/', '-'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertNull(result.timestamp)
        assertEquals(Feet.of(1234), result.altitude)
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
        assertEquals(symbolOf('/', '>'), result.symbol)
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
        assertEquals(symbolOf('/', '>'), result.symbol)
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
        assertEquals(symbolOf('/', '>'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertNull(result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertEquals(20.1253137781, result.range?.value(Miles), 1e-10)
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
        assertEquals(symbolOf('/', '>'), result.symbol)
        assertFalse(result.supportsMessaging)
        assertEquals(expected, result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertEquals(20.1253137781, result.range?.value(Miles), 1e-10)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun nonPosition() {
        val given = ":Hello World"

        assertFails { parser.parse(given) }
    }
}
