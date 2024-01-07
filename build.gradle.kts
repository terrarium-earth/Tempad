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
    id("dev.architectury.loom") version "1.4-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

architectury {
    val minecraftVersion: String by project
    minecraft = minecraftVersion
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")

    val minecraftVersion: String by project
    val modId: String by project
    val modLoader = project.name
    val isCommon = modLoader == rootProject.projects.common.name

    base {
        archivesName.set("$modId-$modLoader-$minecraftVersion")
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
    }

    dependencies {
        val resourcefulLibVersion: String by project
        val resourcefulConfigVersion: String by project
        val botariumVersion: String by project
        val jeiVersion: String by project
        val balmVersion: String by project
        val waystonesVersion: String by project
        val prometheusVersion: String by project
        val argonautsVersion: String by project
        val reiVersion: String by project
        val parchmentMcVersion: String by project

        "minecraft"("::$minecraftVersion")

        @Suppress("UnstableApiUsage")
        "mappings"(project.the<LoomGradleExtensionAPI>().layered {
            val parchmentVersion: String by project

            officialMojangMappings()

            parchment(create(group = "org.parchmentmc.data", name = "parchment-$parchmentMcVersion", version = parchmentVersion))
        })

        compileOnly(group = "com.teamresourceful", name = "yabn", version = "1.0.3")
        "modApi"(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-$modLoader-$minecraftVersion", version = resourcefulLibVersion)
        "modApi"(group = "com.teamresourceful.resourcefulconfig", name = "resourcefulconfig-$modLoader-$minecraftVersion", version = resourcefulConfigVersion)

        if (isCommon) {
            // "modCompileOnly"(group = "earth.terrarium.prometheus", name = "prometheus-$modLoader-1.20", version = prometheusVersion) { isTransitive = false }
            "modCompileOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-api", version = reiVersion)
            "modCompileOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-default-plugin", version = reiVersion)
        } else {
            // "modLocalRuntime"(group = "earth.terrarium.prometheus", name = "prometheus-$modLoader-1.20", version = prometheusVersion)
            "modRuntimeOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-$modLoader", version = reiVersion)
            "modCompileOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-api-$modLoader", version = reiVersion)
            "modCompileOnly"(group = "me.shedaniel", name = "RoughlyEnoughItems-default-plugin-$modLoader", version = reiVersion)
        }

        "modApi"(group = "earth.terrarium.botarium", name = "botarium-$modLoader-$minecraftVersion", version = botariumVersion)

        "modApi"(group = "net.blay09.mods", name = "balm-$modLoader", version = balmVersion) {
            exclude(group = "net.blay09.mods", module = "shared-bridge")
        }

        "modApi"(group = "net.blay09.mods", name = "waystones-$modLoader", version = waystonesVersion) {
            exclude(group = "net.blay09.mods", module = "shared-bridge")
        }

        implementation("io.github.llamalad7:mixinextras-common:0.3.1")
        implementation("io.github.llamalad7:mixinextras-forge:0.3.1")
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

    if (!isCommon) {
        apply(plugin = "com.github.johnrengelman.shadow")
        configure<ArchitectPluginExtension> {
            platformSetupLoomIde()
        }

        val shadowCommon by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        tasks {
            "shadowJar"(ShadowJar::class) {
                archiveClassifier.set("dev-shadow")
                configurations = listOf(shadowCommon)
            }

            "remapJar"(RemapJarTask::class) {
                dependsOn("shadowJar")
                inputFile.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "$modId-$modLoader-$minecraftVersion"
                from(components["java"])

                pom {
                    name.set("Tempad $modLoader")
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
