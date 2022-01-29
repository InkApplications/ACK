package com.inkapplications.ack.cli

object Control {
    const val esc: Char = 27.toChar()
    const val reset = "$esc[0m"

    object Color {
        val lightRed = Color("$esc[1;31m")
        val red = Color("$esc[0;31m")
        val yellow = Color("$esc[1;33m")
        val green = Color("$esc[1;32m")
        val blue = Color("$esc[1;34m")
        val magenta = Color("$esc[1;35m")
    }
}

class Color(private val control: String) {
    fun span(value: String) = "$control$value${Control.reset}"
}
