import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import groovy.json.StringEscapeUtils
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import java.net.URI

plugins {
    java
    id("maven-publish")
    id("com.teamresourceful.resourcefulgradle") version "0.0.+"
    id("dev.architectury.loom") version "1.5-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

apply(plugin = "maven-publish")
apply(plugin = "dev.architectury.loom")
apply(plugin = "architectury-plugin")

val minecraftVersion: String by project
val modId: String by project

base {
    archivesName.set("$modId-$minecraftVersion")
}

configure<LoomGradleExtensionAPI> {
    silentMojangMappingsLicense()
}

repositories {
    maven(url = "https://maven.architectury.dev/")
    maven(url = "https://maven.neoforged.net/releases")
    maven(url = "https://maven.resourcefulbees.com/repository/maven-public/")
    maven(url = "https://maven.twelveiterations.com/repository/maven-public/")
    maven(url = "https://maven.terraformersmc.com/")
    maven(url = "https://maven.ladysnake.org/releases")
    maven {
        url = URI("https://jitpack.io")
        content {
            includeGroup("com.github.LlamaLad7")
            includeGroup("com.github.llamalad7.mixinextras")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        url = uri("https://jm.gserv.me/repository/maven-public/")
        content {
            includeGroup("info.journeymap")
        }
    }
    maven {
        url = uri("https://maven.nucleoid.xyz/")
        content {
            includeGroup("eu.pb4")
        }
    }
}

dependencies {
    val resourcefulLibVersion: String by project
    val resourcefulConfigVersion: String by project
    val botariumVersion: String by project
    val baublyVersion: String by project
    val jeiVersion: String by project
    val balmVersion: String by project
    val waystonesVersion: String by project
    val prometheusVersion: String by project
    val argonautsVersion: String by project
    val reiVersion: String by project
    val parchmentMcVersion: String by project
    val minecraftVersion: String by project
    val neoforgeVersion: String by project
    val curiosVersion: String by project

    "minecraft"("::$minecraftVersion")

    @Suppress("UnstableApiUsage")
    "mappings"(project.the<LoomGradleExtensionAPI>().layered {
        val parchmentVersion: String by project

        officialMojangMappings()

        parchment(create(group = "org.parchmentmc.data", name = "parchment-$parchmentMcVersion", version = parchmentVersion))
    })

    "neoForge"(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)

    "modCompileOnly"("top.theillusivec4.curios:curios-neoforge:${curiosVersion}")
    "modCompileOnly"("top.theillusivec4.curios:curios-neoforge:${curiosVersion}:api")

    compileOnly(group = "com.teamresourceful", name = "yabn", version = "1.0.3")
    "modApi"(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-neoforge-$minecraftVersion", version = resourcefulLibVersion)
    "modApi"(group = "com.teamresourceful.resourcefulconfig", name = "resourcefulconfig-neoforge-$minecraftVersion", version = resourcefulConfigVersion)

    "modImplementation"(group = "me.shedaniel", name = "RoughlyEnoughItems-neoforge", version = reiVersion)
    "modCompileOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-api-neoforge", version = reiVersion)
    "modCompileOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-default-plugin-neoforge", version = reiVersion)

    "modApi"(group = "earth.terrarium.botarium", name = "botarium-neoforge-$minecraftVersion", version = botariumVersion)
    "modImplementation"(group = "earth.terrarium.prometheus", name = "prometheus-neoforge-$minecraftVersion", version = prometheusVersion)
    "modImplementation"(group = "earth.terrarium.argonauts", name = "argonauts-neoforge-$minecraftVersion", version = argonautsVersion)
    "modApi"(group = "earth.terrarium.baubly", name = "baubly-neoforge-$minecraftVersion", version = baublyVersion)

    "modCompileOnly"(group = "net.blay09.mods", name = "balm-neoforge", version = balmVersion) {
        exclude(group = "net.blay09.mods", module = "shared-bridge")
    }

    "modCompileOnly"(group = "net.blay09.mods", name = "waystones-neoforge", version = waystonesVersion) {
        exclude(group = "net.blay09.mods", module = "shared-bridge")
    }
}

java {
    withSourcesJar()
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier.set(null as String?)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    filesMatching(listOf("META-INF/mods.toml", "fabric.mod.json")) {
        expand("version" to project.version)
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
