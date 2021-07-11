plugins {
    id("maven-publish")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    group = "com.inkapplications.karps"
    version = when (project.properties["version"]?.toString()) {
        null, "unspecified", "" -> "1.0-SNAPSHOT"
        else -> project.properties["version"].toString()
    }
    publications {
        withType<MavenPublication> {
            artifact(javadocJar.get())

            pom {
                name.set("Karps ${project.name}")
                description.set("APRS Toolkit")
                url.set("https://github.com/inkapplications/karps")
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
                    connection.set("scm:git:https://github.com/InkApplications/karps.git")
                    developerConnection.set("scm:git:ssh://git@github.com:InkApplications/karps.git")
                    url.set("https://github.com/InkApplications/karps")
                }
            }
        }
    }

    repositories {
        maven {
            name = "Build"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
