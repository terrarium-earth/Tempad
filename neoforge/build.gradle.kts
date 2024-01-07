val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentNeoForge"].extendsFrom(this)
}

dependencies {
    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    val minecraftVersion: String by project
    val neoforgeVersion: String by project
    val curiosVersion: String by project

    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)

    "modRuntimeOnly"("top.theillusivec4.curios:curios-neoforge:${curiosVersion}")
    "modCompileOnly"("top.theillusivec4.curios:curios-neoforge:${curiosVersion}:api")
}
