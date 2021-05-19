enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            from(files(
                "../gradle/libraries.versions.toml"
            ))
        }
    }
}
