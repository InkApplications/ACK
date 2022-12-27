enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ack"

dependencyResolutionManagement {
    versionCatalogs {
        create("thirdParty") {
            from(files("gradle/versions/thirdparty.toml"))
        }
        create("kotlinLibraries") {
            from(files("gradle/versions/kotlin.toml"))
        }
        create("inkLibraries") {
            from(files("gradle/versions/ink.toml"))
        }
    }
}

include("cli")
include("client")
include("codec")
include("structures")

