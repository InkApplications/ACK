plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "com.inkapplications.karps.cli.MainKt"
}

dependencies {
    implementation(projects.client)
    implementation(projects.parser)
    implementation(libraries.coroutines.core)
    implementation("com.github.ajalt:clikt:2.8.0")
}
