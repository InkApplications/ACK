package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.DataExtension
import com.inkapplications.karps.structures.Trajectory
import com.inkapplications.karps.structures.TransmitterInfo
import com.inkapplications.karps.structures.at
import com.inkapplications.karps.structures.unit.*
import kotlin.math.pow

class DataExtensionParser: PacketInformationParser {
    private val extension = Regex("""^(\d{3}|\s{3}|\.{3})/(\d{3}|\s{3}|\.{3})|PHG(\d{4})|RNG(\d{4})|DFS(\d{4})|T(\d{2})/C(\d{2})""")

    override fun parse(data: PacketInformation): PacketInformation {
        val result = extension.find(data.body) ?: return data

        val extension = when {
            result.groupValues[1].isNotEmpty() && result.groupValues[2].isNotEmpty() -> DataExtension.TrajectoryExtra(result.extraAsTrajectory)
            result.groupValues[3].isNotEmpty() -> DataExtension.TransmitterInfoExtra(result.extraAsTransmitterInfo)
            else -> null
        } ?: return data

        return data.copy(
            body = data.body.substring(result.groupValues[0].length),
            extension = extension
        )
    }

    /**
     * Parse a plain Trajectory.
     */
    private val MatchResult.extraAsTrajectory: Trajectory get() {
        val bearing = groupValues[1]
            .takeIf { it.isRelevant() }
            ?.ambiguousValue
            ?.degreesBearing
        val speed = groupValues[2]
            .takeIf { it.isRelevant() }
            ?.ambiguousValue
            ?.mph

        return bearing at speed
    }

    /**
     * Get the transmitter info from a plain extension.
     */
    private val MatchResult.extraAsTransmitterInfo: TransmitterInfo get() {
        val value = groupValues[3]
        val power = value[0].toFloat().pow(2).watts
        val height = 2.0.pow(value[1].toInt()).times(10).feet
        val gain = value[2].toInt().decibels
        val direction = value[3].toInt().times(45).takeIf { it != 0 }?.degreesBearing

        return TransmitterInfo(power, height, gain, direction)
    }
}
