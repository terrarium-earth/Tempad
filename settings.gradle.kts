enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Tempad"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.msrandom.net/repository/cloche")
    }
}

plugins {
    // This plugin allows Gradle to automatically download arbitrary versions of Java for you
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
