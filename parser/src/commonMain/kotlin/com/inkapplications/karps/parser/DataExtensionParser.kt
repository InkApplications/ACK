package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*
import kotlin.math.pow

class DataExtensionParser: PacketInformationParser {
    private val extension = Regex("""^(\d{3}|\s{3}|\.{3})/(\d{3}|\s{3}|\.{3})|PHG(\d{4})|RNG(\d{4})|DFS(\d{4})|T(\d{2})/C(\d{2})""")

    override fun parse(packet: AprsPacket): AprsPacket {
        val result = extension.find(packet.body) ?: return packet

        val extension = when {
            result.groupValues[1].isNotEmpty() && result.groupValues[2].isNotEmpty() -> DataExtension.TrajectoryExtra(result.extraAsTrajectory)
            result.groupValues[3].isNotEmpty() -> DataExtension.TransmitterInfoExtra(result.extraAsTransmitterInfo)
            result.groupValues[4].isNotEmpty() -> DataExtension.RangeExtra(result.extraAsRange)
            result.groupValues[5].isNotEmpty() -> DataExtension.OmniDfSignal(result.extraAsSignal)
            else -> null
        } ?: return packet

        val body = packet.body.substring(result.groupValues[0].length)

        return packet.withBody(body).withExtension(extension)
    }

    /**
     * Parse miles for a precalculated range.
     */
    private val MatchResult.extraAsSignal: SignalInfo get() {
        val value = groupValues[5]
        return SignalInfo(
            strength = value[0].digit.strength,
            height = 2.0.pow(value[1].digit.toInt()).times(10).feet,
            gain = value[2].digit.decibels,
            direction = value[3].digit.times(45).takeIf { it != 0 }?.degreesBearing
        )
    }

    /**
     * Parse miles for a precalculated range.
     */
    private val MatchResult.extraAsRange: Distance get() {
        return groupValues[4].ambiguousValue.miles
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
        val power = value[0].digit.toFloat().pow(2).watts
        val height = 2.0.pow(value[1].digit.toInt()).times(10).feet
        val gain = value[2].digit.decibels
        val direction = value[3].digit.times(45).takeIf { it != 0 }?.degreesBearing

        return TransmitterInfo(power, height, gain, direction)
    }

    private val Char.digit get() = minus(48).toShort()
}
