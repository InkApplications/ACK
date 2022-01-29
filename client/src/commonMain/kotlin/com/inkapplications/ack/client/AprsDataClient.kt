package com.inkapplications.ack.client

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface AprsDataClient {
    suspend fun connect(
        server: String,
        port: Int,
        credentials: Credentials,
        filters: List<String>? = null,
        onConnect: suspend (read: ReceiveChannel<String>, write: SendChannel<String>) -> Unit
    )
}
