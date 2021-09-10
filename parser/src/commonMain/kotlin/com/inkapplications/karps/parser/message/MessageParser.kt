package com.inkapplications.karps.parser.message

import com.inkapplications.karps.parser.PacketTypeParser
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.SpanChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parse
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.toAddress

class MessageParser: PacketTypeParser {
    override val dataTypeFilter: CharArray? = charArrayOf(':')
    private val addresseeParser = SpanChunker(9)
        .mapParsed { it.trim().toAddress() }
    private val startControl = ControlCharacterChunker(':')
    private val endControl = ControlCharacterChunker('{')
    private val messageParser = SpanUntilChunker(
        stopChars = charArrayOf('{'),
        maxLength = 67
    )

    override fun parse(packet: AprsPacket.Unknown): AprsPacket.Message {
        val addressee = addresseeParser.parse(packet)
        val startControl = startControl.parseAfter(addressee)
        val message = messageParser.parseAfter(startControl)
        val endControl = endControl.parseOptionalAfter(message)

        return AprsPacket.Message(
            raw = packet.raw,
            received = packet.received,
            dataTypeIdentifier = packet.dataTypeIdentifier,
            source = packet.source,
            destination = packet.destination,
            digipeaters = packet.digipeaters,
            addressee = addressee.result,
            message = message.result,
            messageNumber = runCatching { endControl.remainingData.toInt() }.getOrNull()
        )
    }
}
