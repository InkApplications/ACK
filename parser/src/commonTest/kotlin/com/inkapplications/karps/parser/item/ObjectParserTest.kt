package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObjectParserTest {
    @Test
    fun liveObject() {
        val given = "LEADER   *092345z4903.50N/07201.75W>088/036"

        val result = ObjectParser().parse(TestData.prototype.copy(body = given))

        assertTrue(result is AprsPacket.ObjectReport)
        assertEquals("LEADER", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals("092345z4903.50N/07201.75W>088/036", result.body)
    }

    @Test
    fun killedObject() {
        val given = "LEADER   _092345z4903.50N/07201.75W>088/036"

        val result = ObjectParser().parse(TestData.prototype.copy(body = given))

        assertTrue(result is AprsPacket.ObjectReport)
        assertEquals("LEADER", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals("092345z4903.50N/07201.75W>088/036", result.body)
    }

    @Test
    fun nonObject() {
        val given = "LEA_092345z4903.50N/07201.75W>088/036"

        val result = ObjectParser().parse(TestData.prototype.copy(body = given))

        assertFalse(result is AprsPacket.ObjectReport)
        assertEquals(given, result.body)
    }
}
