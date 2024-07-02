enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Tempad"

pluginManagement {
    repositories {
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.neoforged.net/releases/")
        maven(url = "https://maven.resourcefulbees.com/repository/maven-public/")
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}