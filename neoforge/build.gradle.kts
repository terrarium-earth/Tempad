architectury {
    neoForge()
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentNeoForge"].extendsFrom(this)
}

dependencies {
    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(path = ":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }

    val minecraftVersion: String by project
    val neoforgeVersion: String by project
    val curiosVersion: String by project

    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)

    modCompileOnly("top.theillusivec4.curios:curios-neoforge:${curiosVersion}")
    modCompileOnly("top.theillusivec4.curios:curios-neoforge:${curiosVersion}:api")
    modLocalRuntime("com.teamresourceful:yabn:1.0.3")
    modLocalRuntime("com.teamresourceful:bytecodecs:1.0.2")
}
