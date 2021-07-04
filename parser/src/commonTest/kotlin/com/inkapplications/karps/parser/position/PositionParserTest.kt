package com.inkapplications.karps.parser.position

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.parser.timestamp.withUtcValues
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class PositionParserTest {
    @Test
    fun plainPosition() {
        val given = "4903.50N/07201.75W-Test 001234"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals("Test 001234", result.comment)
        assertEquals(symbolOf('/', '-'), result.symbol)
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
        val given = "4903.50N/07201.75W#PHG5132Test 001234"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals("Test 001234", result.comment)
        assertEquals(symbolOf('/', '#'), result.symbol)
        assertEquals(25.watts, result.transmitterInfo?.power)
        assertEquals(20.feet, result.transmitterInfo?.height)
        assertEquals(3.decibels, result.transmitterInfo?.gain)
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
        val given = "4903.50N/07201.75W-Test/A=001234"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals("Test", result.comment)
        assertEquals(symbolOf('/', '-'), result.symbol)
        assertNull(result.timestamp)
        assertEquals(1234.feet, result.altitude)
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun plainTimestamp() {
        val given = "092345z4903.50N/07201.75W>Test1234"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0291, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals("Test1234", result.comment)
        assertEquals(symbolOf('/', '>'), result.symbol)
        assertEquals(expected, result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertNull(result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)

    }

    @Test
    fun compressedPosition() {
        val given = "/5L!!<*e7> sTComment"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        assertEquals(49.5, result.coordinates.latitude.asDecimal, 0.1)
        assertEquals(-72.75, result.coordinates.longitude.asDecimal, 0.1)
        assertEquals("Comment", result.comment)
        assertEquals(symbolOf('/', '>'), result.symbol)
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
        val given = "/5L!!<*e7>{?!Comment"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        assertEquals(49.5, result.coordinates.latitude.asDecimal, 0.1)
        assertEquals(-72.75, result.coordinates.longitude.asDecimal, 0.1)
        assertEquals("Comment", result.comment)
        assertEquals(symbolOf('/', '>'), result.symbol)
        assertNull(result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertEquals(20.1253137781.miles, result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun compressedTimestamp() {
        val given = "092345z/5L!!<*e7>{?!Comment"

        val result = PositionParser().parse(TestData.prototype.copy(body = given))

        val expected = Clock.System.now()
            .withUtcValues(
                dayOfMonth = 9,
                hour = 23,
                minute = 45,
                second = 0,
                nanosecond = 0
            )

        assertEquals(49.5, result.coordinates.latitude.asDecimal, 0.1)
        assertEquals(-72.75, result.coordinates.longitude.asDecimal, 0.1)
        assertEquals("Comment", result.comment)
        assertEquals(symbolOf('/', '>'), result.symbol)
        assertEquals(expected, result.timestamp)
        assertNull(result.altitude)
        assertNull(result.trajectory)
        assertEquals(20.1253137781.miles, result.range)
        assertNull(result.transmitterInfo)
        assertNull(result.signalInfo)
        assertNull(result.directionReportExtra)
    }

    @Test
    fun nonPosition() {
        val given = "Hello World"

        assertFails { PositionParser().parse(TestData.prototype.copy(body = given)) }
    }
}
