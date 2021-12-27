package com.inkapplications.karps.parser.message

import com.inkapplications.karps.parser.PacketDataTransformer
import com.inkapplications.karps.parser.chunk.common.ControlCharacterChunker
import com.inkapplications.karps.parser.chunk.common.SpanChunker
import com.inkapplications.karps.parser.chunk.common.SpanUntilChunker
import com.inkapplications.karps.parser.chunk.mapParsed
import com.inkapplications.karps.parser.chunk.parseAfter
import com.inkapplications.karps.parser.chunk.parseOptionalAfter
import com.inkapplications.karps.parser.format.fixedLength
import com.inkapplications.karps.parser.format.leftPad
import com.inkapplications.karps.parser.requireType
import com.inkapplications.karps.structures.EncodingConfig
import com.inkapplications.karps.structures.PacketData
import com.inkapplications.karps.structures.toAddress

class MessageTransformer: PacketDataTransformer {
    private val dataTypeCharacter = ':'
    private val dataTypeChunker = ControlCharacterChunker(dataTypeCharacter)
    private val addresseeParser = SpanChunker(9)
        .mapParsed { it.trim().toAddress() }
    private val startControl = ControlCharacterChunker(':')
    private val endControl = ControlCharacterChunker('{')
    private val messageParser = SpanUntilChunker(
        stopChars = charArrayOf('{'),
        maxLength = 67
    )

    override fun parse(body: String): PacketData.Message {
        val dataTypeIdentifier = dataTypeChunker.popChunk(body)
        val addressee = addresseeParser.parseAfter(dataTypeIdentifier)
        val startControl = startControl.parseAfter(addressee)
        val message = messageParser.parseAfter(startControl)
        val endControl = endControl.parseOptionalAfter(message)

        return PacketData.Message(
            addressee = addressee.result,
            message = message.result,
            messageNumber = runCatching { endControl.remainingData.toInt() }.getOrNull()
        )
    }

    override fun generate(packet: PacketData, config: EncodingConfig): String {
        packet.requireType<PacketData.Message>()
        val addressee = packet.addressee.toString().fixedLength(9)
        val number = packet.messageNumber?.let { "{${it.leftPad(3)}" }.orEmpty()

        return "$dataTypeCharacter${addressee}:${packet.message}$number"
    }
}
