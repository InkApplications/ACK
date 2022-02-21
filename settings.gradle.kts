enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ack"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            from(fileTree("gradle/versions").matching {
                include("*.toml")
            })
        }
    }
}

include("cli")
include("client")
include("codec")
include("structures")

