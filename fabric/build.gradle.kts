architectury {
    fabric()
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentFabric"].extendsFrom(this)
}

dependencies {
    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    val minecraftVersion: String by project
    val fabricLoaderVersion: String by project
    val fabricApiVersion: String by project
    val modMenuVersion: String by project
    val trinketsVersion: String by project

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = fabricLoaderVersion)
    modApi(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "$fabricApiVersion+$minecraftVersion")

    modApi(group = "com.terraformersmc", name = "modmenu", version = modMenuVersion)
    // modImplementation(group = "maven.modrinth", name = "fwaystones", version = "3.1.2+mc1.20")
    // modImplementation(group = "maven.modrinth", name = "owo-lib", version = "0.11.1+1.20")
    modImplementation(group = "maven.modrinth", name = "energized-power", version = "gDKMKHBG")
    // "modImplementation"(group = "dev.emi", name = "trinkets", version = trinketsVersion)
}
