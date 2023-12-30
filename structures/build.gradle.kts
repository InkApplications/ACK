plugins {
    id("multiplatform.tier2")
    id("ink.publishing")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlinLibraries.kotlinx.datetime.core)
                api(inkLibraries.spondee.units)
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
