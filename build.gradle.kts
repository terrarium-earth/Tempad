plugins {
    id("earth.terrarium.cloche") version "0.18.11"
    kotlin("jvm") version "2.3.20"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    cloche {
        main()

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
    }
}

cloche {
    minecraftVersion = "26.1.2"

    metadata {
        modId = "example"
        name = "Tempad"
        license = "MIT (for code) + ARR (for everything else)"
        description = ""

        dependencies {
            require("kotlinforforge", "6.1.0a")
            require("resourcefullib", "4.0.0")
            require("resourcefulconfig", "4.0.1")
        }
    }

    neoforge {
        loaderVersion = "26.1.2.5-beta"

        data()

        runs {
            server()
            client()
            data()
        }

        dependencies {
            // Mod dependencies: Kotlin for Forge, ResourcefulLib, ResourcefulConfig, Olympus (included)
            implementation(module(group = "thedarkcolour", name = "kotlinforforge-neoforge", version = "6.1.0a"))
            implementation(module(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-neoforge-26.1", version = "4.0.0"))
            implementation(module(group = "com.teamresourceful.resourcefulconfig", name = "resourcefulconfig-neoforge-26.1", version = "4.0.1"))
            implementation(module(group = "com.teamresourceful", name = "bytecodecs", version = "1.1.0"))
            val olympus = module(group = "earth.terrarium.olympus", name = "olympus-neoforge-26.1", version = "1.8.0")
            implementation(olympus)
            include(olympus)

            // JEI
            implementation(module(group = "mezz.jei", name = "jei-26.1.2-neoforge", version = "29.5.0.24"))

            // Jade
            implementation(module(group = "maven.modrinth", name = "nvQzSEkH", version = "xp9l9JJG"))

            // FTB Teams
            implementation(module(group = "dev.ftb.mods", name = "ftb-teams-neoforge", version = "26.1.0.2-SNAPSHOT"))

            // Lambda's Dynamic Lights // dev.lambdaurora.lambdynamiclights:lambdynamiclights-api:4.10.1+26.1.1
            implementation(module(group = "dev.lambdaurora.lambdynamiclights", name = "lambdynamiclights-api", version = "4.10.1+26.1.1"))
            runtimeOnly(module(group = "dev.lambdaurora.lambdynamiclights", name = "lambdynamiclights-runtime", version = "4.10.1+26.1.1"))

            //
        }
    }
}