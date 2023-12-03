architectury {
    forge()
}

loom {
    forge {
        mixinConfig("tempad-common.mixins.json")
        mixinConfig("tempad.mixins.json")
    }
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentForge"].extendsFrom(this)
}

dependencies {
    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }

    val minecraftVersion: String by project
    val forgeVersion: String by project

    forge(group = "net.neoforged", name = "forge", version = "$minecraftVersion-$forgeVersion")
}
