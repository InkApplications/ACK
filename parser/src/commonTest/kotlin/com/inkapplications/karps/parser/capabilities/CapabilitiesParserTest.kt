package com.inkapplications.karps.parser.capabilities

import com.inkapplications.karps.parser.TestData
import com.inkapplications.karps.structures.Capability
import kotlin.test.Test
import kotlin.test.assertTrue

class CapabilitiesParserTest {
    @Test
    fun parse() {
        val given = "IGate, MSG_CNT=123, LOC_CNT=asdf"

        val result = CapabilitiesParser().parse(TestData.prototype.copy(body = given))

        assertTrue(Capability.Token("IGate") in result.capabilityData, "Expected IGate token in ${result.capabilityData}")
        assertTrue(Capability.Value("MSG_CNT", "123") in result.capabilityData, "Expected MSG_CNT in ${result.capabilityData}")
        assertTrue(Capability.Value("LOC_CNT", "asdf") in result.capabilityData)
    }
}
