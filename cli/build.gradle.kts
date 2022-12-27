plugins {
    application
    kotlin("jvm")
    id("com.jakewharton.mosaic")
}

application {
    mainClassName = "com.inkapplications.ack.cli.MainKt"
}

dependencies {
    implementation(projects.client)
    implementation(projects.codec)
    implementation(kotlinLibraries.coroutines.core)
    implementation("com.github.ajalt.clikt:clikt:3.2.0")
}
