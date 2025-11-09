import com.diffplug.gradle.spotless.BaseKotlinExtension

plugins {
    id("maven-publish")
    id("dev.architectury.loom") version "1.13-SNAPSHOT" apply false
    kotlin("jvm") version "2.0.21" apply false

    // https://github.com/ReplayMod/preprocessor
    // https://github.com/Fallen-Breath/preprocessor
    id("com.replaymod.preprocess") version "d452ef76"

    // https://github.com/Fallen-Breath/yamlang
    id("me.fallenbreath.yamlang") version "1.5.0" apply false

    id("com.diffplug.spotless") version "8.0.0"
}

tasks.named("assemble").get().dependsOn("spotlessApply")

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://jitpack.io")
}

@Suppress("LocalVariableName", "ktlint:standard:property-naming")
preprocess {
    // it doesn't seem to be possible to explicitly set the descriptor of a field,
    // which we would need for strict mappings to work
    strictExtraMappings = false

    val mc12006_common = createNode("1.20.6-common", 1_20_06, "yarn")
    val mc12006_fabric = createNode("1.20.6-fabric", 1_20_06, "yarn")
    val mc12006_neoforge = createNode("1.20.6-neoforge", 1_20_06, "yarn")

    // 1.20.6
    mc12006_common.link(mc12006_fabric, null)
    mc12006_common.link(mc12006_neoforge, file("versions/mappings-common-neoforge.txt"))
}

spotless {
    fun BaseKotlinExtension.customKtlint() = ktlint("1.2.1").editorConfigOverride(
        mapOf(
            "ktlint_standard_no-wildcard-imports" to "disabled",
            "ktlint_standard_blank-line-before-declaration" to "disabled",
            "ktlint_standard_spacing-between-declarations-with-annotations" to "disabled",
            // these are replaced by the custom rule set
            "ktlint_standard_import-ordering" to "disabled",
            "ktlint_standard_comment-spacing" to "disabled",
            "ktlint_standard_chain-wrapping" to "disabled",
        ),
    ).customRuleSets(listOf("com.github.RubixDev:ktlint-ruleset-mc-preprocessor:2c5a3687bb"))

    kotlinGradle {
        target("**/*.gradle.kts")
        customKtlint()
    }
    kotlin {
        target("**/src/*/kotlin/**/*.kt")
        // disable formatting between `//#if` and `//#endif` including any space in front of them
        // unless they are at the start of a line (which should only be the case in imports)
        toggleOffOnRegex("([ \\t]+//#if[\\s\\S]*?[ \\t]+//#endif)")
        customKtlint()
    }
    java {
        target("**/src/*/java/**/*.java")
        // disable formatting between `//#if` and `//#endif` including any space in front of them
        toggleOffOnRegex("([ \\t]*//#if[\\s\\S]*?[ \\t]*//#endif)")
        // TODO: importOrder()
        removeUnusedImports()
        eclipse("4.31").configFile("eclipse-prefs.xml")
        formatAnnotations()
    }
}

tasks.register("buildAndGather") {
    subprojects {
        dependsOn(project.tasks.named("build").get())
    }
    doFirst {
        println("Gathering builds")

        fun buildLibs(p: Project) = p.layout.buildDirectory.get().asFile.toPath().resolve("libs")
        delete(
            fileTree(buildLibs(rootProject)) {
                include("*")
            },
        )
        subprojects {
            if (!project.name.endsWith("-common")) {
                copy {
                    from(buildLibs(project)) {
                        include("*.jar")
                        exclude("*-dev.jar", "*-sources.jar")
                    }
                    into(buildLibs(rootProject))
                    duplicatesStrategy = DuplicatesStrategy.INCLUDE
                }
            }
        }
    }
}
