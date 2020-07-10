plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "com.inkapplications.karps.cli.MainKt"
}

dependencies {
    implementation(project(":client"))
    implementation(kotlin("stdlib"))
    implementation(Coroutines.core)
    implementation("com.github.ajalt:clikt:2.8.0")
}
