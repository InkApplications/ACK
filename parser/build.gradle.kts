plugins {
    id("library.multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libraries.coroutines.core)
                api(projects.structures)
                api(libraries.kimchi.logger)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libraries.kotlin.reflect)
                implementation(libraries.kotlin.test.core)
                implementation(libraries.kotlin.test.annotations)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(libraries.coroutines.core)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libraries.kotlin.reflect)
                implementation(libraries.kotlin.test.junit)
                implementation("com.squareup.moshi:moshi-kotlin:1.9.3")
            }
        }
    }
}
