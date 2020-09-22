package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemParserTest {
    @Test
    fun liveObject() {
        val given = "AID #2!4903.50N/07201.75WA"

        val result = ItemParser().parse(TestData.prototype.copy(body = given))

        assertTrue(result is AprsPacket.ItemReport)
        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals("4903.50N/07201.75WA", result.body)
    }

    @Test
    fun killedObject() {
        val given = "AID #2_4903.50N/07201.75WA"

        val result = ItemParser().parse(TestData.prototype.copy(body = given))

        assertTrue(result is AprsPacket.ItemReport)
        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals("4903.50N/07201.75WA", result.body)
    }

    @Test
    fun nontItem() {
        val result = ItemParser().parse(TestData.Position.expected)

        assertFalse(result is AprsPacket.ItemReport)
        assertEquals(TestData.Position.expected.body, result.body)
    }
}
