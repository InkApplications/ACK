package com.inkapplications.karps.client

fun generatePasscode(address: String): Int {
    var hash = 0x73e2
    address
        .substringBefore('-')
        .uppercase()
        .toCharArray()
        .forEachIndexed { index, c ->
            if (index % 2 == 0) {
                hash = hash.xor(c.code.shl(8))
            } else {
                hash = hash.xor(c.code)
            }
        }

    return hash.and(0x7fff)
}
