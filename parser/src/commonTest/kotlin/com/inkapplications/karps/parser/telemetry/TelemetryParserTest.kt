package com.inkapplications.karps.parser.telemetry

import com.inkapplications.karps.parser.TestData
import kotlin.test.Test
import kotlin.test.assertEquals

class TelemetryParserTest {
    @Test
    fun parse() {
        val given = "#005,199,000,255,073,123,01101001Hello World"

        val result = TelemetryParser().parse(TestData.prototype.copy(body = given))

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
        val given = "#MIC199,000,255,073,123,01101001Hello World"

        val result = TelemetryParser().parse(TestData.prototype.copy(body = given))

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
        val given = "#005,1.2,2.3,3.4,4.5,5.6,01101001Hello World"

        val result = TelemetryParser().parse(TestData.prototype.copy(body = given))

        assertEquals("005", result.sequenceId)
        assertEquals(1.2f, result.data.analog1)
        assertEquals(2.3f, result.data.analog2)
        assertEquals(3.4f, result.data.analog3)
        assertEquals(4.5f, result.data.analog4)
        assertEquals(5.6f, result.data.analog5)
        assertEquals(105, result.data.digital.toInt())
        assertEquals("Hello World", result.comment)
    }
}
