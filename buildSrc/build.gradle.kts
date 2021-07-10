plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencies {
    implementation(libraries.kotlin.gradle)
    implementation("com.jakewharton.mosaic:mosaic-gradle-plugin:0.1.0")
}
