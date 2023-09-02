package com.inkapplications.ack.structures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SymbolTest {
    @Test
    fun validSymbols() {
        Symbol.Schema.validate("!/")
        Symbol.Schema.validate("~/")
        Symbol.Schema.validate("!\\")
        Symbol.Schema.validate("~\\")
        Symbol.Schema.validate("#A")
        Symbol.Schema.validate("#Z")
        Symbol.Schema.validate("#a")
        Symbol.Schema.validate("#z")
        Symbol.Schema.validate("#9")
        Symbol.Schema.validate("#9")
    }

    @Test
    fun invalidSymbols() {
        assertFails { Symbol.Schema.validate("") }
        assertFails { Symbol.Schema.validate("  ") }
        assertFails { Symbol.Schema.validate(" ") }
        assertFails { Symbol.Schema.validate("!/ ") }
        assertFails { Symbol.Schema.validate("\uD83C\uDF55") }
    }

    @Test
    fun createSymbol() {
        assertEquals(Symbol.Primary('!'), symbolOf("!/"))
        assertEquals(Symbol.Alternate('!'), symbolOf("!\\"))
        assertEquals(Symbol.Alternate('!', 'B'), symbolOf("!B"))
    }

    @Test
    fun codes() {
        assertEquals("!/", Symbol.Primary('!').code)
        assertEquals("!\\", Symbol.Alternate('!').code)
        assertEquals("!B", Symbol.Alternate('!', 'B').code)
    }
}
