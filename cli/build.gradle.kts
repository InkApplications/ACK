plugins {
    application
    kotlin("jvm")
    id("com.jakewharton.mosaic")
}

application {
    mainClassName = "com.inkapplications.karps.cli.MainKt"
}

dependencies {
    implementation(projects.client)
    implementation(projects.parser)
    implementation(libraries.coroutines.core)
    implementation("com.github.ajalt.clikt:clikt:3.2.0")
}
