package com.inkapplications.ack.structures

data class TelemetryValues(
    val analog1: Float,
    val analog2: Float,
    val analog3: Float,
    val analog4: Float,
    val analog5: Float,
    val digital: UByte,
) {
    val analogValues = arrayOf(analog1, analog2, analog3, analog4, analog5)
}
