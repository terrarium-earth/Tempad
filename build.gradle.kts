import groovy.json.StringEscapeUtils
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
    id("com.teamresourceful.resourcefulgradle") version "0.0.+"
    id("net.neoforged.gradle.userdev") version "7.0.145"
}

val minecraftVersion: String by project
val modId: String by project

base {
    archivesName.set("$modId-$minecraftVersion")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    maven(url = "https://maven.architectury.dev/")
    maven(url = "https://maven.neoforged.net/releases")
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://maven.twelveiterations.com/repository/maven-public/")
    maven(url = "https://maven.octo-studios.com/releases")
    mavenLocal()
}

dependencies {
    val neoforgeVersion: String by project
    val minecraft_version: String by project

    val resourcefulConfigVersion: String by project
    val resourcefulLibVersion: String by project
    val resourcefulLibKtVersion: String by project
    val kotlinForForgeVersion: String by project
    val curiosVersion: String by project

    implementation("net.neoforged:neoforge:${neoforgeVersion}")

    implementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-neoforge-${minecraft_version}:${resourcefulConfigVersion}")
    implementation("com.teamresourceful.resourcefullib:resourcefullib-neoforge-${minecraft_version}:${resourcefulLibVersion}")
    compileOnly("com.teamresourceful:bytecodecs:1.1.0")
    implementation("thedarkcolour:kotlinforforge-neoforge:${kotlinForForgeVersion}")
    implementation("com.teamresourceful.resourcefullibkt:resourcefullibkt-neoforge-${minecraft_version}:${resourcefulLibKtVersion}") {
        isTransitive = false
    }

    implementation(group = "earth.terrarium.olympus", name = "olympus-neoforge-$minecraftVersion", version = "1.0.1") {
        isTransitive = false
    }

    implementation("top.theillusivec4.curios:curios-neoforge:${curiosVersion}")
}

java {
    withSourcesJar()
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand("version" to project.version)
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjvm-default=all")
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "$modId-$minecraftVersion"
            from(components["java"])

            pom {
                name.set("Tempad")
                url.set("https://github.com/terrarium-earth/$modId")

                scm {
                    connection.set("git:https://github.com/terrarium-earth/$modId.git")
                    developerConnection.set("git:https://github.com/terrarium-earth/$modId.git")
                    url.set("https://github.com/terrarium-earth/$modId")
                }

                licenses {
                    license {
                        name.set("ARR")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            setUrl("https://maven.resourcefulbees.com/repository/terrarium/")
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_PASS")
            }
        }
    }
}

resourcefulGradle {
    templates {
        register("embed") {
            val minecraftVersion: String by project
            val version: String by project
            val changelog: String = file("changelog.md").readText(Charsets.UTF_8)
            val fabricLink: String? = System.getenv("FABRIC_RELEASE_URL")
            val forgeLink: String? = System.getenv("FORGE_RELEASE_URL")

            source.set(file("templates/embed.json.template"))
            injectedValues.set(mapOf(
                    "minecraft" to minecraftVersion,
                    "version" to version,
                    "changelog" to StringEscapeUtils.escapeJava(changelog),
                    "fabric_link" to fabricLink,
                    "forge_link" to forgeLink
            ))
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}