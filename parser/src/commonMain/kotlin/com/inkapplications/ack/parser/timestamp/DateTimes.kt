package com.inkapplications.ack.parser.timestamp

import kotlinx.datetime.*

/**
 * Copy constructor for LocalDateTime objects
 */
internal fun LocalDateTime.with(
    year: Int? = null,
    month: Month? = null,
    dayOfMonth: Int? = null,
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null,
    nanosecond: Int? = null
): LocalDateTime {
    return LocalDateTime(
        year ?: this.year,
        month ?: this.month,
        dayOfMonth ?: this.dayOfMonth,
        hour ?: this.hour,
        minute ?: this.minute,
        second ?: this.second,
        nanosecond ?: this.nanosecond
    )
}

/**
 * Copy constructor for Instants
 */
internal fun Instant.withUtcValues(
    year: Int? = null,
    month: Month? = null,
    dayOfMonth: Int? = null,
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null,
    nanosecond: Int? = null
): Instant {
    return toLocalDateTime(TimeZone.UTC)
        .with(
            year,
            month,
            dayOfMonth,
            hour,
            minute,
            second,
            nanosecond,
        )
        .toInstant(TimeZone.UTC)
}
