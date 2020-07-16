package com.inkapplications.karps.parser

import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import com.soywiz.klock.TimezoneOffset

const val TIMESTAMP = """(?:[0-9]{6}[0-9z/h]|[0-9]{8})"""

class Timestamps(
    private val timezone: TimezoneOffset = TimezoneOffset.local(DateTime.now())
) {
    private val DHMZ = Regex("""(0[1-9]|[1-2][0-9]|3[01])([01][0-9]|2[0-4])([0-5][0-9])[Zz]""")
    private val DHML = Regex("""(0[1-9]|[1-2][0-9]|3[01])([01][0-9]|2[0-4])([0-5][0-9])/""")
    private val HMS = Regex("""([01][0-9]|2[0-4])([0-5][0-9])([0-5][0-9])[Hh]""")
    private val MDHM = Regex("""(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[01])([01][0-9]|2[0-4])([0-5][0-9])""")

    fun parse(data: String): DateTime {
        DHMZ.find(data)?.run {
            val (days, hours, minutes) = destructured
            return DateTime.now()
                .copyDayOfMonth(
                    dayOfMonth = days.toInt(),
                    hours = hours.toInt(),
                    minutes = minutes.toInt(),
                    seconds = 0
                )
        }
        HMS.find(data)?.run {
            val (hours, minutes, seconds) = destructured
            return DateTime.now()
                .copyDayOfMonth(
                    hours = hours.toInt(),
                    minutes = minutes.toInt(),
                    seconds = seconds.toInt()
                )
        }
        MDHM.find(data)?.run {
            val (month, days, hours, minutes) = destructured
            return DateTime.now()
                .copyDayOfMonth(
                    month = Month.get(month.toInt()),
                    dayOfMonth = days.toInt(),
                    hours = hours.toInt(),
                    minutes = minutes.toInt(),
                    seconds = 0
                )
        }
        DHML.find(data)?.run {
            val (days, hours, minutes) = destructured
            return DateTime.now()
                .copyDayOfMonth(
                    dayOfMonth = days.toInt(),
                    hours = hours.toInt(),
                    minutes = minutes.toInt(),
                    seconds = 0
                )
                .toOffsetUnadjusted(timezone)
                .utc
        }

        throw IllegalArgumentException("Illegal date format: $data")
    }
}
