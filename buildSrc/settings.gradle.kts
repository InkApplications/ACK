enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("thirdParty") {
            from(files("../gradle/versions/thirdparty.toml"))
        }
        create("kotlinLibraries") {
            from(files("../gradle/versions/kotlin.toml"))
        }
        create("inkLibraries") {
            from(files("../gradle/versions/ink.toml"))
        }
    }
}
