import groovy.json.JsonSlurper

pluginManagement {
    repositories {
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://jitpack.io")
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.replaymod.preprocess" -> {
                    useModule("com.github.Fallen-Breath:preprocessor:${requested.version}")
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
val settings = JsonSlurper().parseText(rootDir.resolve("settings.json").readText()) as Map<String, List<String>>
for (version in settings["versions"]!!) {
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../common.gradle.kts"
    }
}
