import org.gradle.api.tasks.testing.logging.TestExceptionFormat

repositories {
    mavenCentral()
    google()
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }

    tasks.withType(Test::class) {
        testLogging.exceptionFormat = TestExceptionFormat.FULL
        testLogging.showStandardStreams = true
    }
}
