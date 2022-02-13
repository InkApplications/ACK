plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencies {
    implementation(libraries.kotlin.gradle)
    implementation(libraries.mosaic)
    implementation(libraries.ink.publishing)
}
