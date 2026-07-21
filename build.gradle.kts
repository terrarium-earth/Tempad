import net.peanuuutz.tomlkt.TomlArray
import net.peanuuutz.tomlkt.TomlLiteral
import net.peanuuutz.tomlkt.asTomlArray
import net.peanuuutz.tomlkt.asTomlTable
import net.peanuuutz.tomlkt.buildTomlTable

plugins {
    kotlin("jvm") version "2.4.0"
    id("earth.terrarium.cloche") version "0.19.12"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    cloche {
        main()

        mavenLocal()
        mavenFabric()
        mavenNeoforgedMeta()
        mavenNeoforged()

        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://maven.teamresourceful.com/repository/maven-public/")
        maven("https://maven.blamejared.com")
        maven("https://api.modrinth.com/maven")
        maven("https://maven.ftb.dev/snapshots")
        maven("https://maven.ftb.dev/releases")
        maven("https://maven.gegy.dev")
        maven("https://maven.theillusivec4.top/")
        maven("https://repo.nyon.dev/releases")
    }
}

cloche {
    minecraftVersion = "26.1.2"

    metadata {
        modId = "tempad"
        name = "Tempad"
        license = "MIT (for code) + ARR (for everything else)"
        description = ""

        dependencies {
            require("kotlinforforge", "6.1.0a")
            require("resourcefullib", "4.0.0")
            require("resourcefulconfig", "4.0.1")
        }
    }

    singleTarget {
        neoforge {
            loaderVersion = "26.1.2.77"

            data()

            runs {
                server()
                client()
                clientData()
                data()
            }

            metadata {
                modLoader = "kotlinforforge"
                withToml {
                    withElement {
                        buildTomlTable {
                            for ((key, value) in entries) {
                                if (key != "mods") {
                                    element(key, value)
                                    continue
                                }

                                val mod = value.asTomlArray()[0].asTomlTable()

                                val modifiedMod = buildTomlTable {
                                    for ((key, value) in mod) {
                                        element(key, value)
                                    }

                                    element("enumExtensions", TomlLiteral("META-INF/enum_extensions.json"))
                                }

                                element(key, TomlArray(modifiedMod))
                            }
                        }
                    }
                }
            }

            dependencies {
                // Mod dependencies: Kotlin for Forge, ResourcefulLib, ResourcefulConfig, Olympus (included)
                implementation("thedarkcolour:kotlinforforge-neoforge:6.3.0")

                implementation(module(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-neoforge-26.1", version = "4.0.0"))
                val rlibKit = module(group = "com.teamresourceful.resourcefullibkt", name = "resourcefullibkt-26.1", version = "3.0.0")
                implementation(rlibKit)
                include(rlibKit)

                implementation(module(group = "com.teamresourceful.resourcefulconfig", name = "resourcefulconfig-neoforge-26.1", version = "4.0.1"))
                val rconfigKt = module(group = "com.teamresourceful.resourcefulconfigkt", name = "resourcefulconfigkt-26.1-rc-1", version = "4.0.0-beta.1")
                // implementation(rconfigKt)
                // include(rconfigKt)

                implementation(module(group = "com.teamresourceful", name = "bytecodecs", version = "1.1.0"))
                val olympus = module(group = "earth.terrarium.olympus", name = "olympus-neoforge-26.1", version = "1.8.1")
                implementation(olympus)
                include(olympus)

                // JEI
                implementation(module(group = "mezz.jei", name = "jei-26.1.2-neoforge", version = "29.5.0.24"))

                // Jade
                implementation(module(group = "maven.modrinth", name = "nvQzSEkH", version = "xp9l9JJG"))

                // FTB Teams
                implementation(module(group = "dev.ftb.mods", name = "ftb-teams-neoforge", version = "26.1.0.2-SNAPSHOT"))

                /*
                // Lambda's Dynamic Lights // dev.lambdaurora.lambdynamiclights:lambdynamiclights-api:4.10.1+26.1.1
                implementation(module(group = "dev.lambdaurora.lambdynamiclights", name = "lambdynamiclights-api", version = "4.10.1+26.1.1"))
                runtimeOnly(module(group = "dev.lambdaurora.lambdynamiclights", name = "lambdynamiclights-runtime", version = "4.10.1+26.1.1"))
                 */

                runtimeOnly("top.theillusivec4.curios:curios-neoforge:15.0.0-beta.2+26.1.2")
                compileOnly("top.theillusivec4.curios:curios-neoforge:15.0.0-beta.2+26.1.2:api")
                //
            }
        }
    }
}