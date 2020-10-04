import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    }
}

subprojects {
    group = "com.github.inkapplications.karps"
    version = if (version != "unspecified") version else "1.0-SNAPSHOT"

    repositories {
        jcenter()
        maven(url = "https://jitpack.io")
    }

    tasks.withType(Test::class) {
        testLogging.exceptionFormat = TestExceptionFormat.FULL
    }
}
