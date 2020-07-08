package com.inkapplications.karps.client

actual object AprsClientModule {
    actual fun createDataClient(): AprsDataClient = KtorAprsDataClient(
        "karps 0.0.0"
    )
}
