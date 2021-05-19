plugins {
    id("library.multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libraries.kotlinx.datetime.core)
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
