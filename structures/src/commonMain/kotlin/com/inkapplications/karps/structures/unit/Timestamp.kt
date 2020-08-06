package com.inkapplications.karps.structures.unit

inline class Timestamp(val epochMilliseconds: Long)

val Long.asTimestamp get() = Timestamp(this)
