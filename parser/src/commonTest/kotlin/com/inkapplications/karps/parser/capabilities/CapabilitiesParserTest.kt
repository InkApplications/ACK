package com.inkapplications.karps.parser.capabilities

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Capability
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CapabilitiesParserTest {
    @Test
    fun parse() {
        val given = "IGate, MSG_CNT=123, LOC_CNT=asdf"

        val result = CapabilitiesTransformer().parse(TestData.prototype.copy(body = given))

        assertTrue(Capability.Token("IGate") in result.capabilityData, "Expected IGate token in ${result.capabilityData}")
        assertTrue(Capability.Value("MSG_CNT", "123") in result.capabilityData, "Expected MSG_CNT in ${result.capabilityData}")
        assertTrue(Capability.Value("LOC_CNT", "asdf") in result.capabilityData)
    }

    @Test
    fun encode() {
        val given = AprsPacket.CapabilityReport(
            dataTypeIdentifier = TestData.prototype.dataTypeIdentifier,
            source = TestData.prototype.source,
            destination = TestData.prototype.destination,
            digipeaters = TestData.prototype.digipeaters,
            capabilityData = setOf(
                Capability.Token("IGate"),
                Capability.Value("MSG_CNT", "123"),
                Capability.Value("LOC_CNT", "asdf"),
            )
        )

        val result = CapabilitiesTransformer().generate(given)

        assertEquals("IGate,MSG_CNT=123,LOC_CNT=asdf", result)
    }
}
