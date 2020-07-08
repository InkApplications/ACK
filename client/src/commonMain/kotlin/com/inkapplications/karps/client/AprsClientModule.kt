package com.inkapplications.karps.client

expect object AprsClientModule {
    fun createDataClient(): AprsDataClient
}
