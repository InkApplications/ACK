package com.inkapplications.karps.structures

private const val CHAR_RANGE_START = '!'
private const val CHAR_RANGE_END = '~'
private val SUPPORTS_OVERLAY = arrayOf('#','&','0','>','A','W','^','_','s','u','v','z')
private val VALID_OVERLAY = ('0'..'9') + ('a'..'j') + ('A'..'Z') + '/' + '\\'

/**
 * APRS Symbol data to describe a graphical symbol.
 *
 * Contains the data necessary to look up a symbol for APRS from
 * Primary/Secondary tables and overlays.
 *
 * http://www.aprs.org/symbols.html
 */
sealed class Symbol {
    /**
     * Symbol on the Primary table.
     */
    data class Primary(val id: Char): Symbol() {
        init {
            require(id in CHAR_RANGE_START..CHAR_RANGE_END) {
                "Code Identifier '$id' out of bounds."
            }
        }
    }

    /**
     * Alternate-table symbol, and possible overlay.
     *
     * @param id The base symbol to use
     * @param overlay symbol indicating the overlay to use.
     */
    data class Alternate(val id: Char, val overlay: Char? = null): Symbol() {
        /**
         * Whether the base symbol supports alphanumeric overlays.
         */
        val alphaNumeric = id in SUPPORTS_OVERLAY

        init {
            require(id in CHAR_RANGE_START..CHAR_RANGE_END) {
                "Code Identifier '$id' out of bounds."
            }
            require(overlay == null || overlay in VALID_OVERLAY) {
                "Table Identifier '$overlay' out of bounds."
            }
        }
    }
}

/**
 * Create a symbol from a table/code pair.
 *
 * @param tableIdentifier Character identifying the table or an overlay for
 *        alternate symbols.
 * @param codeIdentifier Character identifying the base symbol on the specified table.
 */
fun symbolOf(tableIdentifier: Char, codeIdentifier: Char): Symbol {
    return when (tableIdentifier) {
        '/' -> Symbol.Primary(codeIdentifier)
        '\\' -> Symbol.Alternate(codeIdentifier)
        else -> Symbol.Alternate(codeIdentifier, tableIdentifier)
    }
}
