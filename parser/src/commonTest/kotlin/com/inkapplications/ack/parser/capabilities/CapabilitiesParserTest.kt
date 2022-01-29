package com.inkapplications.ack.parser.capabilities

import com.inkapplications.ack.structures.Capability
import com.inkapplications.ack.structures.PacketData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CapabilitiesParserTest {
    @Test
    fun parse() {
        val given = "<IGate, MSG_CNT=123, LOC_CNT=asdf"

        val result = CapabilitiesTransformer().parse(given)

        assertTrue(Capability.Token("IGate") in result.capabilityData, "Expected IGate token in ${result.capabilityData}")
        assertTrue(Capability.Value("MSG_CNT", "123") in result.capabilityData, "Expected MSG_CNT in ${result.capabilityData}")
        assertTrue(Capability.Value("LOC_CNT", "asdf") in result.capabilityData)
    }

    @Test
    fun encode() {
        val given = PacketData.CapabilityReport(
            capabilityData = setOf(
                Capability.Token("IGate"),
                Capability.Value("MSG_CNT", "123"),
                Capability.Value("LOC_CNT", "asdf"),
            )
        )

        val result = CapabilitiesTransformer().generate(given)

        assertEquals("<IGate,MSG_CNT=123,LOC_CNT=asdf", result)
    }
}
