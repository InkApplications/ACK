package com.inkapplications.ack.structures

data class EncodingConfig(
    val compression: EncodingPreference = EncodingPreference.None,
    val weatherDataFillCharacter: Char = '.',
)
