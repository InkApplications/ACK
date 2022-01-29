package com.inkapplications.ack.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

internal class KtorAprsDataClient(
    private val version: String
): AprsDataClient {
    private val receiveChannel = Channel<String>(Channel.BUFFERED)
    private val sendChannel = Channel<String>(Channel.RENDEZVOUS)

    override suspend fun connect(
        server: String,
        port: Int,
        credentials: Credentials,
        filters: List<String>?,
        onConnect: suspend (ReceiveChannel<String>, SendChannel<String>) -> Unit
    ) {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(server, port))
        val socketWrite = socket.openWriteChannel(autoFlush = true)
        val socketRead = socket.openReadChannel()
        val commands = filters?.let { "filter ${it.joinToString(" ")}" } ?: ""
        coroutineScope {
            launch {
                while (isActive && !socketRead.isClosedForRead) {
                    socketRead.readLine {
                        receiveChannel.offer(it)
                    }
                }
            }
            launch {
                sendChannel.consumeEach {
                    if (socketWrite.isClosedForWrite) {
                        cancel()
                    } else {
                        socketWrite.write("$it\r\n")
                    }
                }
            }
            sendChannel.send("user ${credentials.callsign} pass ${credentials.passcode} vers $version $commands")
            onConnect(receiveChannel, sendChannel)
        }
    }

    private suspend inline fun ByteReadChannel.readLine(process: (String) -> Unit) {
        val buffer = ByteBuffer.allocate(333)
        val sentinel = readUntilDelimiter(ByteBuffer.wrap("\r\n".toByteArray()), buffer)
        if (sentinel == -1) return
        val line = String(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset() + buffer.position(), StandardCharsets.US_ASCII)
        discardExact(2)
        buffer.clear()
        process(line)
    }
}
