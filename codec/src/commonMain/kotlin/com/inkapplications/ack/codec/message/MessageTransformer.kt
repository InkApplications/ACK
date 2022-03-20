package com.inkapplications.ack.codec.message

import com.inkapplications.ack.codec.PacketDataTransformer
import com.inkapplications.ack.codec.chunk.common.ControlCharacterChunker
import com.inkapplications.ack.codec.chunk.common.SpanChunker
import com.inkapplications.ack.codec.chunk.common.SpanUntilChunker
import com.inkapplications.ack.codec.chunk.mapParsed
import com.inkapplications.ack.codec.chunk.parseAfter
import com.inkapplications.ack.codec.chunk.parseOptionalAfter
import com.inkapplications.ack.codec.format.fixedLength
import com.inkapplications.ack.codec.format.leftPad
import com.inkapplications.ack.codec.requireType
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.toStationAddress

class MessageTransformer: PacketDataTransformer {
    private val dataTypeCharacter = ':'
    private val dataTypeChunker = ControlCharacterChunker(dataTypeCharacter)
    private val addresseeParser = SpanChunker(9)
        .mapParsed { it.toStationAddress() }
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
