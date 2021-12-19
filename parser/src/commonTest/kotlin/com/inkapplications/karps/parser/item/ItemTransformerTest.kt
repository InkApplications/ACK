package com.inkapplications.karps.parser.item

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.parser.assertEquals
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.ReportState
import com.inkapplications.karps.structures.TransmitterInfo
import com.inkapplications.karps.structures.symbolOf
import inkapplications.spondee.measure.Bels
import inkapplications.spondee.measure.Feet
import inkapplications.spondee.measure.Watts
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.of
import kotlin.test.*

class ItemTransformerTest {
    @Test
    fun liveItem() {
        val given = ")AID #2!4903.50N/07201.75WA"

        val result = ItemTransformer().parse(TestData.route, given)

        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(symbolOf('/', 'A'), result.symbol)
    }

    @Test
    fun killedItem() {
        val given = ")AID #2_4903.50N/07201.75WA"

        val result = ItemTransformer().parse(TestData.route, given)

        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(symbolOf('/', 'A'), result.symbol)
    }

    @Test
    fun nonItem() {
        assertFails { ItemTransformer().parse(TestData.route, "!3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/") }
    }

    @Test
    fun liveItemGenerate() {
        val given = AprsPacket.ItemReport(
            route = TestData.route,
            name = "AID #2",
            state = ReportState.Live,
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0292).longitude),
            symbol = symbolOf('/', 'A'),
            comment = "Hello World",
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = TransmitterInfo(
                power = Watts.of(25),
                height = Feet.of(20),
                gain = Bels.of(Deci, 3),
                direction = inkapplications.spondee.spatial.Cardinal.East.toAngle(),
            ),
            signalInfo = null,
            directionReport = null,
        )

        val result = ItemTransformer().generate(given)

        assertEquals(")AID #2!4903.50N/07201.75WAPHG5132Hello World", result)
    }

    @Test
    fun killedItemGenerate() {
        val given = AprsPacket.ItemReport(
            route = TestData.route,
            name = "AID #2",
            state = ReportState.Kill,
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0292).longitude),
            symbol = symbolOf('/', 'A'),
            comment = "Hello World",
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = TransmitterInfo(
                power = Watts.of(25),
                height = Feet.of(20),
                gain = Bels.of(Deci, 3),
                direction = inkapplications.spondee.spatial.Cardinal.East.toAngle(),
            ),
            signalInfo = null,
            directionReport = null,
        )

        val result = ItemTransformer().generate(given)

        assertEquals(")AID #2_4903.50N/07201.75WAPHG5132Hello World", result)
    }
}
