plugins {
    kotlin("multiplatform")
    id("ink.publishing")
}
kotlin {
    jvmToolchain(11)
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlinLibraries.coroutines.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:1.3.1")
            }
        }
    }
}
