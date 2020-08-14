package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.DataExtension
import com.inkapplications.karps.structures.QualityReport
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.unit.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataExtensionParserTest {
    @Test
    fun parseTrajectory() {
        val input = TestData.prototype.copy(
            body = "123/456Hello World"
        )
        val result = DataExtensionParser().parse(input)
        val resultExtension = result.extension

        assertTrue(resultExtension is DataExtension.TrajectoryExtra)
        assertEquals(Trajectory(123.degreesBearing, 456.mph), resultExtension.value)
        assertEquals("Hello World", result.body)
    }

    @Test
    fun parseRange() {
        val input = TestData.prototype.copy(
            body = "RNG0123Hello World"
        )
        val result = DataExtensionParser().parse(input)
        val resultExtension = result.extension

        assertTrue(resultExtension is DataExtension.RangeExtra)
        assertEquals(123.miles, resultExtension.value)
        assertEquals("Hello World", result.body)
    }

    @Test
    fun parseStationInfo() {
        val input = TestData.prototype.copy(
            body = "PHG2234Hello World"
        )
        val result = DataExtensionParser().parse(input)
        val resultExtension = result.extension

        assertTrue(resultExtension is DataExtension.TransmitterInfoExtra)
        assertEquals(4.watts, resultExtension.value.power)
        assertEquals(40.feet, resultExtension.value.height)
        assertEquals(3.decibels, resultExtension.value.gain)
        assertEquals(180.degreesBearing, resultExtension.value.direction)
        assertEquals("Hello World", result.body)
    }

    @Test
    fun parseSignalInfo() {
        val input = TestData.prototype.copy(
            body = "DFS2361Hello World"
        )
        val result = DataExtensionParser().parse(input)
        val resultExtension = result.extension

        assertTrue(resultExtension is DataExtension.OmniDfSignal)
        assertEquals(2.strength, resultExtension.value.strength)
        assertEquals(80.feet, resultExtension.value.height)
        assertEquals(6.decibels, resultExtension.value.gain)
        assertEquals(45.degreesBearing, resultExtension.value.direction)
        assertEquals("Hello World", result.body)
    }
    @Test
    fun parseDfReport() {
        val input = TestData.prototype.copy(
            body = "123/456/270/726Hello World"
        )
        val result = DataExtensionParser().parse(input)
        val resultExtension = result.extension

        assertTrue(resultExtension is DataExtension.DirectionReportExtra)
        assertEquals(Trajectory(123.degreesBearing, 456.mph), resultExtension.trajectory)
        assertEquals(QualityReport(7, 4.miles, 8.degreesBearing), resultExtension.quality)
        assertEquals("Hello World", result.body)
    }
}