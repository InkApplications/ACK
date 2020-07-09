plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api(Coroutines.common)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib"))
                api(Coroutines.core)
            }
        }
    }
}
