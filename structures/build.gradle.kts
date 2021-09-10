plugins {
    id("library.multiplatform")
    id("library.publish")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libraries.kotlinx.datetime.core)
                api("com.inkapplications.spondee:measures:0.3.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libraries.kotlin.test.core)
                implementation(libraries.kotlin.test.annotations)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libraries.kotlin.test.junit)
            }
        }
    }
}
