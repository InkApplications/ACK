package com.inkapplications.karps.parser.timestamp

import com.inkapplications.karps.parser.PacketFormatException
import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import kotlin.test.*

class CompositeTimestampParserTest {
    @Test
    fun allCall() {
        val first = ParserSpy()
        val second = ParserSpy()
        val composite = CompositeTimestampParser(first, second)

        val result = runCatching { composite.parse("not-real") }

        assertTrue(result.isFailure, "Exception thrown when no parser can parse")
        assertTrue(first.called, "All parsers are invoked")
        assertTrue(second.called, "All parsers are invoked")

    }

    @Test
    fun orderedPriority() {
        val first = ParserSpy()
        val second = object: TimestampParser {
            override fun parse(timestamp: String): Timestamp {
                return 123L.asTimestamp
            }
        }
        val third = ParserSpy()
        val composite = CompositeTimestampParser(first, second, third)

        val result = composite.parse("not-real")

        assertEquals(123L, result.epochMilliseconds)
        assertTrue(first.called, "First parser should be invoked if called in-order")
        assertFalse(third.called, "Third parser should not be invoked if a result was provided.")

    }
}

private class ParserSpy: TimestampParser {
    var called = false
    override fun parse(timestamp: String): Timestamp {
        called = true
        throw PacketFormatException()
    }
}
