package com.inkapplications.ack.client

expect object AprsClientModule {
    fun createDataClient(): AprsDataClient
}
