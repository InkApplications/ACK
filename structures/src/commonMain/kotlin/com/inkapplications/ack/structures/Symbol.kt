package com.inkapplications.ack.structures

import com.inkapplications.ack.structures.Symbol.Schema.ALTERNATE_TABLE
import com.inkapplications.ack.structures.Symbol.Schema.PRIMARY_TABLE
import com.inkapplications.ack.structures.Symbol.Schema.ALPHANUMERIC_OVERLAYS
import com.inkapplications.ack.structures.Symbol.Schema.SYMBOL_RANGE
import com.inkapplications.ack.structures.Symbol.Schema.VALID_OVERLAY

/**
 * APRS Symbol data to describe a graphical symbol.
 *
 * Contains the data necessary to look up a symbol for APRS from
 * Primary/Secondary tables and overlays.
 *
 * http://www.aprs.org/symbols.html
 */
sealed interface Symbol {
    val id: Char

    /**
     * Symbol on the Primary table.
     */
    data class Primary(override val id: Char): Symbol {
        init {
            Schema.validate(id, PRIMARY_TABLE)
        }

        companion object {
            /**
             * All possible primary symbols.
             */
            val ALL = SYMBOL_RANGE.map { Primary(it) }
        }
    }

    /**
     * Alternate-table symbol, and possible overlay.
     *
     * @param id The base symbol to use
     * @param overlay symbol indicating the overlay to use.
     */
    data class Alternate(override val id: Char, val overlay: Char? = null): Symbol {
        /**
         * Whether the base symbol supports alphanumeric overlays.
         */
        val alphaNumeric = id in ALPHANUMERIC_OVERLAYS

        init {
            Schema.validate(id, overlay ?: ALTERNATE_TABLE)
            require(overlay == null || overlay in VALID_OVERLAY) {
                "Overlay Identifier '$overlay' out of bounds."
            }
        }

        companion object {
            /**
             * All of the base alternate symbols, not including overlays.
             */
            val BASE = SYMBOL_RANGE.map { Alternate(it) }
        }
    }

    object Schema {
        /**
         * The character used to indicate the primary table.
         */
        const val PRIMARY_TABLE = '/'

        /**
         * The character used to indicate the alternate table.
         */
        const val ALTERNATE_TABLE = '\\'

        /**
         * The first character in the ASCII range that can be used for symbols.
         */
        const val CHAR_RANGE_START = '!'

        /**
         * The last character in the ASCII range that can be used for symbols.
         */
        const val CHAR_RANGE_END = '~'

        /**
         * The range of ASCII characters that can be used for symbols.
         */
        val SYMBOL_RANGE = CHAR_RANGE_START..CHAR_RANGE_END

        /**
         * List of symbol IDs that support alphanumeric overlays.
         */
        val ALPHANUMERIC_OVERLAYS = arrayOf('#','&','0','>','A','W','^','_','s','u','v','z')

        /**
         * List of valid overlay characters.
         */
        val VALID_OVERLAY = ('0'..'9') + ('a'..'z') + ('A'..'Z')

        /**
         * Validate whether a string is a valid symbol code.
         */
        fun validate(code: String) {
            require(code.length == 2) { "Symbol Code must be 2 characters long." }
            val (id, table) = code.toCharArray()
            validate(id, table)
        }

        /**
         * Validate whether a symbol id/table pair is valid.
         */
        fun validate(id: Char, table: Char) {
            require(id in SYMBOL_RANGE) {
                "Identifier '$id' out of bounds."
            }
            require(table == PRIMARY_TABLE || table == ALTERNATE_TABLE || table in VALID_OVERLAY) {
                "Table Identifier '$table' out of bounds."
            }
        }
    }
}

/**
 * Convert a symbol to its id/table characters.
 */
fun Symbol.toIdTablePair() = when {
    this is Symbol.Primary -> id to PRIMARY_TABLE
    this is Symbol.Alternate && overlay != null -> id to overlay
    else -> id to ALTERNATE_TABLE
}

/**
 * Get the two-character code string for the Symbol.
 */
val Symbol.code: String get() = toIdTablePair().let { "${it.first}${it.second}" }

/**
 * Create a symbol from a two-character code.
 *
 * @param code Two-character code identifying an APRS symbol
 * @throws IllegalArgumentException if the code does not match a symbol schema.
 * @return APRS symbol corresponding to the code.
 */
fun symbolOf(code: String): Symbol {
    Symbol.Schema.validate(code)
    return symbolOf(code[0], code[1])
}

/**
 * Create a symbol from a two-character code.
 *
 * @param code Two-character code identifying an APRS symbol
 * @return APRS symbol corresponding to the code, or null if the code is invalid.
 */
fun symbolOfOrNull(code: String): Symbol? {
    return try {
        symbolOf(code)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Create a symbol from a id/table pair.
 *
 * @param table Character identifying the table or an overlay for
 *        alternate symbols.
 * @param id Character identifying the base symbol on the specified table.
 */
fun symbolOf(id: Char, table: Char): Symbol {
    return when (table) {
        PRIMARY_TABLE -> Symbol.Primary(id)
        ALTERNATE_TABLE -> Symbol.Alternate(id)
        else -> Symbol.Alternate(id, table)
    }
}

@Deprecated("Renamed and inverted. Use toIdTablePair() instead, remembering to flip the pair.")
fun Symbol.toTableCodePair() = when {
    this is Symbol.Primary -> PRIMARY_TABLE to id
    this is Symbol.Alternate && overlay != null -> overlay to id
    else -> ALTERNATE_TABLE to id
}
