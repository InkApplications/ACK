package com.inkapplications.ack.client

actual object AprsClientModule {
    actual fun createDataClient(): AprsDataClient = KtorAprsDataClient(
        "ack 0.0.0"
    )
}
