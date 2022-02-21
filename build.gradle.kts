import org.gradle.api.tasks.testing.logging.TestExceptionFormat

subprojects {
    repositories {
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx/")
    }

    tasks.withType(Test::class) {
        testLogging.exceptionFormat = TestExceptionFormat.FULL
        testLogging.showStandardStreams = true
    }
}
