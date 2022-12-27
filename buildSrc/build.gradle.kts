plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencies {
    implementation(kotlinLibraries.kotlin.gradle)
    implementation(thirdParty.mosaic)
    implementation(inkLibraries.ink.publishing)
}
