package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.soywiz.klock.DateTime
import kotlin.test.*

class CompositeTimestampParserTest {
    private val prototype = AprsPacket.Unknown(
        received = DateTime.now(),
        dataTypeIdentifier = '!',
        source = Address("KE0YOG", "7"),
        digipeaters = emptyList(),
        destination = Address("KE0YOG", "8"),
        body = "hello world"
    )

    @Test
    fun allCall() {
        val first = ParserSpy('!')
        val second = ParserSpy(',', '!')
        val composite = CompositeInformationParser(first, second)

        val result = runCatching { composite.parse(prototype) }

        assertTrue(result.isFailure, "Exception thrown when no parser can parse")
        assertTrue(first.called, "All parsers are invoked")
        assertTrue(second.called, "All parsers are invoked")

    }

    @Test
    fun orderedPriority() {
        val first = ParserSpy('!')
        val second = object: ParserSpy('!') {
            override fun parse(packet: AprsPacket.Unknown): AprsPacket {
                called = true
                return packet.copy(body = "test")
            }
        }
        val third = ParserSpy('!')
        val composite = CompositeInformationParser(first, second, third)

        val result = composite.parse(prototype)

        assertTrue(result is AprsPacket.Unknown)
        assertEquals("test", result.body)
        assertTrue(first.called, "First parser should be invoked if called in-order")
        assertTrue(second.called, "Second parser should be invoked.")
        assertFalse(third.called, "Third parser should not be invoked if a result was provided.")

    }

    @Test
    fun filtering() {
        val first = ParserSpy('!')
        val second = ParserSpy(',')
        val composite = CompositeInformationParser(first, second)

        runCatching { composite.parse(prototype) }

        assertTrue(first.called, "Parser supporting data type is invoked")
        assertFalse(second.called, "Parser not supporting type is not invoked")

    }
}

private open class ParserSpy(
    override vararg val supportedDataTypes: Char
): PacketInformationParser {
    var called = false

    override fun parse(packet: AprsPacket.Unknown): AprsPacket {
        called = true
        throw PacketFormatException()
    }
}
