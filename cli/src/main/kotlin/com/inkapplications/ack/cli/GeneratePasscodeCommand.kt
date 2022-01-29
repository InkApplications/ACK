package com.inkapplications.ack.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.inkapplications.ack.client.generatePasscode
import kotlin.system.exitProcess

class GeneratePasscodeCommand: CliktCommand() {
    val agreeToTerms: Boolean by option(
        help = "Pass in this flag if you agree to the terms of service",
    ).flag()

    val callsign: String? by option(
        help = "The callsign for your radio license",
    )

    override fun run() {
        val agreed = this.agreeToTerms
            || prompt("This passcode is for licensed radio operators. It is your responsibility to know how to use the service properly In no event shall the authors of this software be liable for any claim, damages or any liability, whether in action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software. If you agree, type 'Yes'")
                .let { it?.trim()?.lowercase() == "yes" }

        if (!agreed) {
            echo("Cannot proceed without agreeing to terms of use", err = true)
            exitProcess(2)
        }

        val callsign = callsign
            ?: prompt("Enter the callsign for your radio license")
            ?: exitProcess(3)

        echo(generatePasscode(callsign))
    }
}
