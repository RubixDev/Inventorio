import com.diffplug.gradle.spotless.BaseKotlinExtension

plugins {
    id("maven-publish")
    id("dev.architectury.loom") version "1.5-SNAPSHOT" apply false
    // TODO: the preprocessor doesn't yet work with Kotlin 1.9
    // https://github.com/ReplayMod/remap/pull/17
    kotlin("jvm") version "1.8.22" apply false

    // https://github.com/ReplayMod/preprocessor
    // https://github.com/Fallen-Breath/preprocessor
    id("com.replaymod.preprocess") version "ce1aeb2b"

    // https://github.com/Fallen-Breath/yamlang
    id("me.fallenbreath.yamlang") version "1.3.1" apply false

    id("com.diffplug.spotless") version "6.25.0"
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
    val mc12001_common = createNode("1.20.1-common", 1_20_01, "yarn")
    val mc12001_fabric = createNode("1.20.1-fabric", 1_20_01, "yarn")
    val mc12001_forge = createNode("1.20.1-forge", 1_20_01, "yarn")

    val mc12002_fabric = createNode("1.20.2-fabric", 1_20_02, "yarn")
    val mc12002_neoforge = createNode("1.20.2-neoforge", 1_20_02, "yarn")

    val mc12004_common = createNode("1.20.4-common", 1_20_04, "yarn")
    val mc12004_fabric = createNode("1.20.4-fabric", 1_20_04, "yarn")
    val mc12004_neoforge = createNode("1.20.4-neoforge", 1_20_04, "yarn")

    // 1.20.1
    mc12002_fabric.link(mc12001_fabric, null)
    mc12004_common.link(mc12001_common, null)
    mc12001_common.link(mc12001_forge, file("versions/mappings-common-forge.txt"))
    // 1.20.2
    mc12004_fabric.link(mc12002_fabric, null)
    mc12004_neoforge.link(mc12002_neoforge, null)
    // 1.20.4
    mc12004_common.link(mc12004_fabric, null)
    mc12004_common.link(mc12004_neoforge, file("versions/mappings-common-neoforge.txt"))
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
