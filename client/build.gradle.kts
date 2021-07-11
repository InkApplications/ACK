plugins {
    id("library.multiplatform")
    id("library.publish")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libraries.coroutines.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:1.3.1")
            }
        }
    }
}
