package com.inkapplications.ack.codec.item

import com.inkapplications.ack.codec.assertEquals
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.ReportState
import com.inkapplications.ack.structures.TransmitterInfo
import com.inkapplications.ack.structures.symbolOf
import inkapplications.spondee.measure.metric.watts
import inkapplications.spondee.measure.us.feet
import inkapplications.spondee.scalar.bels
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.spatial.toAngle
import inkapplications.spondee.structure.Deci
import inkapplications.spondee.structure.scale
import kotlin.test.*

class ItemTransformerTest {
    @Test
    fun liveItem() {
        val given = ")AID #2!4903.50N/07201.75WA"

        val result = ItemTransformer().parse(given)

        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Live, result.state)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(symbolOf('/', 'A'), result.symbol)
    }

    @Test
    fun killedItem() {
        val given = ")AID #2_4903.50N/07201.75WA"

        val result = ItemTransformer().parse(given)

        assertEquals("AID #2", result.name)
        assertEquals(ReportState.Kill, result.state)
        assertEquals(49.0583, result.coordinates.latitude.asDecimal, 0.0001)
        assertEquals(-72.0292, result.coordinates.longitude.asDecimal, 0.0001)
        assertEquals(symbolOf('/', 'A'), result.symbol)
    }

    @Test
    fun nonItem() {
        assertFails { ItemTransformer().parse("!3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/") }
    }

    @Test
    fun liveItemGenerate() {
        val given = PacketData.ItemReport(
            name = "AID #2",
            state = ReportState.Live,
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0292).longitude),
            symbol = symbolOf('/', 'A'),
            comment = "Hello World",
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = TransmitterInfo(
                power = 25.watts,
                height = 20.feet,
                gain = 3.scale(Deci).bels,
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
        val given = PacketData.ItemReport(
            name = "AID #2",
            state = ReportState.Kill,
            coordinates = GeoCoordinates(49.0583.latitude, (-72.0292).longitude),
            symbol = symbolOf('/', 'A'),
            comment = "Hello World",
            altitude = null,
            trajectory = null,
            range = null,
            transmitterInfo = TransmitterInfo(
                power = 25.watts,
                height = 20.feet,
                gain = 3.scale(Deci).bels,
                direction = inkapplications.spondee.spatial.Cardinal.East.toAngle(),
            ),
            signalInfo = null,
            directionReport = null,
        )

        val result = ItemTransformer().generate(given)

        assertEquals(")AID #2_4903.50N/07201.75WAPHG5132Hello World", result)
    }
}
