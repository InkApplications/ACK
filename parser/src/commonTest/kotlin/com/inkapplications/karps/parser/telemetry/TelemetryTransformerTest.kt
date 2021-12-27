package com.inkapplications.karps.parser.telemetry

import com.inkapplications.karps.structures.PacketData
import com.inkapplications.karps.structures.TelemetryValues
import kotlin.test.Test
import kotlin.test.assertEquals

class TelemetryTransformerTest {
    @Test
    fun parse() {
        val given = "T#005,199,000,255,073,123,01101001Hello World"

        val result = TelemetryTransformer().parse(given)

        assertEquals("005", result.sequenceId)
        assertEquals(199, result.data.analog1.toInt())
        assertEquals(0, result.data.analog2.toInt())
        assertEquals(255, result.data.analog3.toInt())
        assertEquals(73, result.data.analog4.toInt())
        assertEquals(123, result.data.analog5.toInt())
        assertEquals(105, result.data.digital.toInt())
        assertEquals("Hello World", result.comment)
    }

    @Test
    fun parseMic() {
        val given = "T#MIC199,000,255,073,123,01101001Hello World"

        val result = TelemetryTransformer().parse(given)

        assertEquals("MIC", result.sequenceId)
        assertEquals(199, result.data.analog1.toInt())
        assertEquals(0, result.data.analog2.toInt())
        assertEquals(255, result.data.analog3.toInt())
        assertEquals(73, result.data.analog4.toInt())
        assertEquals(123, result.data.analog5.toInt())
        assertEquals(105, result.data.digital.toInt())
        assertEquals("Hello World", result.comment)
    }

    @Test
    fun parseFloats() {
        val given = "T#005,1.2,2.3,3.4,4.5,5.6,01101001Hello World"

        val result = TelemetryTransformer().parse(given)

        assertEquals("005", result.sequenceId)
        assertEquals(1.2f, result.data.analog1)
        assertEquals(2.3f, result.data.analog2)
        assertEquals(3.4f, result.data.analog3)
        assertEquals(4.5f, result.data.analog4)
        assertEquals(5.6f, result.data.analog5)
        assertEquals(105, result.data.digital.toInt())
        assertEquals("Hello World", result.comment)
    }

    @Test
    fun generate() {
        val given = PacketData.TelemetryReport(
            sequenceId = "666",
            data = TelemetryValues(1.2f, 2.3f, 3.4f, 4.5f, 5.6f, 105u),
            comment = "Hello World",
        )

        val result = TelemetryTransformer().generate(given)

        assertEquals("T#666,1.2,2.3,3.4,4.5,5.6,01101001Hello World", result)
    }

    @Test
    fun generateMic() {
        val given = PacketData.TelemetryReport(
            sequenceId = "MIC",
            data = TelemetryValues(1.2f, 2.3f, 3.4f, 4.5f, 5.6f, 105u),
            comment = "Hello World",
        )

        val result = TelemetryTransformer().generate(given)

        assertEquals("T#MIC,1.2,2.3,3.4,4.5,5.6,01101001Hello World", result)
    }

    @Test
    fun generateInts() {
        val given = PacketData.TelemetryReport(
            sequenceId = "000",
            data = TelemetryValues(123f, 456f, 789f, 0f, 246.8f, 0u),
            comment = "",
        )

        val result = TelemetryTransformer().generate(given)

        assertEquals("T#000,123,456,789,000,247,00000000", result)
    }
}
