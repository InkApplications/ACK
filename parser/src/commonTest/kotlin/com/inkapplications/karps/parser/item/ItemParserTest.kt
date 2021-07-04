package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.ReportState
import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.structures.symbolOf
import kotlin.test.*

class ItemParserTest {
    @Test
    fun liveItem() {
        val given = "AID #2!4903.50N/07201.75WA"

        val result = ItemParser().parse(TestData.prototype.copy(body = given))

        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(symbolOf('/', 'A'), result.symbol)
    }

    @Test
    fun killedItem() {
        val given = "AID #2_4903.50N/07201.75WA"

        val result = ItemParser().parse(TestData.prototype.copy(body = given))

        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(symbolOf('/', 'A'), result.symbol)
    }

    @Test
    fun nontItem() {
        assertFails { ItemParser().parse(TestData.prototype.copy(body = "3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/")) }
    }
}
