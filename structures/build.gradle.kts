plugins {
    id("library.multiplatform")
    id("com.inkapplications.publishing")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlinLibraries.kotlinx.datetime.core)
                api("com.inkapplications.spondee:measures:0.3.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlinLibraries.kotlin.test.core)
                implementation(kotlinLibraries.kotlin.test.annotations)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlinLibraries.kotlin.test.junit)
            }
        }
    }
}
