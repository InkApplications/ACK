package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.structures.ReportState
import com.inkapplications.karps.structures.at
import com.inkapplications.karps.structures.unit.degreesBearing
import com.inkapplications.karps.structures.unit.knots
import com.soywiz.klock.DateTime
import kotlin.test.*

class ObjectParserTest {
    @Test
    fun liveObject() {
        val given = "LEADER   *092345z4903.50N/07201.75W>088/036"

        val result = ObjectParser().parse(TestData.prototype.copy(body = given))
        val resultDateTime = result.timestamp?.epochMilliseconds?.let { DateTime.fromUnix(it) }

        assertEquals("LEADER", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals(9, resultDateTime?.dayOfMonth)
        assertEquals(23, resultDateTime?.hours)
        assertEquals(45, resultDateTime?.minutes)
        assertEquals(49.0583, result.coordinates.latitude.decimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.decimal, 0.0001)
        assertEquals(88.degreesBearing at 36.knots, result.trajectory)
    }

    @Test
    fun killedObject() {
        val given = "LEADER   _092345z4903.50N/07201.75W>088/036"

        val result = ObjectParser().parse(TestData.prototype.copy(body = given))
        val resultDateTime = result.timestamp?.epochMilliseconds?.let { DateTime.fromUnix(it) }

        assertEquals("LEADER", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals(9, resultDateTime?.dayOfMonth)
        assertEquals(23, resultDateTime?.hours)
        assertEquals(45, resultDateTime?.minutes)
        assertEquals(49.0583, result.coordinates.latitude.decimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.decimal, 0.0001)
        assertEquals(88.degreesBearing at 36.knots, result.trajectory)
    }

    @Test
    fun nonObject() {
        val given = "LEA_092345z4903.50N/07201.75W>088/036"

        assertFails { ObjectParser().parse(TestData.prototype.copy(body = given)) }
    }
}
