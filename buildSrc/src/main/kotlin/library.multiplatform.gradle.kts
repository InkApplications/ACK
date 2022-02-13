plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    jvm()
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("ACK ${project.name}")
                description.set("APRS Client for Kotlin")
                url.set("https://ack.inkapplications.com")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://choosealicense.com/licenses/mit/")
                    }
                }
                developers {
                    developer {
                        id.set("reneevandervelde")
                        name.set("Renee Vandervelde")
                        email.set("Renee@InkApplications.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/InkApplications/ack.git")
                    developerConnection.set("scm:git:ssh://git@github.com:InkApplications/ack.git")
                    url.set("https://github.com/InkApplications/ack")
                }
            }
        }
    }
}
