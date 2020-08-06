package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.unit.Timestamp
import com.inkapplications.karps.structures.unit.asTimestamp
import com.soywiz.klock.DateTime

/**
 * Provides a date/time reading.
 */
interface Clock {
    val current: Timestamp
}

/**
 * Default clock implementation that provides the current time.
 */
object SystemClock: Clock {
    override val current get() = DateTime.now().unixMillisLong.asTimestamp
}

/**
 * Clock locked to a specific time.
 *
 * This is mostly useful for testing and forces the time to be locked to a
 * specific time.
 */
class FixedClock(override val current: Timestamp): Clock
