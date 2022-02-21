package com.inkapplications.ack.parser.chunk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ChunkTest {
    @Test
    fun mapParsed() {
        val given = Chunk(2, "Remaining")

        val result = given.mapParsed { it + 1 }

        assertEquals(3, result.result)
        assertEquals("Remaining", result.remainingData)
    }

    @Test
    fun requireEndValid() {
        val given = Chunk("Test", "")

        given.requireEnd()
    }

    @Test
    fun requireEndInvalid() {
        val given = Chunk("Test", "Uh oh..")

        assertFails { given.requireEnd() }
    }
}
