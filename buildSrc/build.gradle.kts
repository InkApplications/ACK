plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}
dependencies {
    implementation(kotlinLibraries.kotlin.gradle)
    implementation(thirdParty.mosaic)
    implementation(thirdParty.dokka)
    implementation(thirdParty.kotlinx.binary.compatibility)
}
