import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("maven-publish")
    id("dev.architectury.loom")
    kotlin("jvm")
    id("com.replaymod.preprocess")
    id("me.fallenbreath.yamlang")
}

val loaderName = if (project.name.endsWith("-common")) "common" else loom.platform.get().name.lowercase()
assert(loaderName in listOf("common", "fabric", "forge", "neoforge"))
assert(project.name.endsWith("-$loaderName"))
enum class Loader {
    COMMON,
    FABRIC,
    FORGE,
    NEOFORGE,
    ;

    val isCommon get() = this == COMMON
    val isFabric get() = this == FABRIC
    val isForge get() = this == FORGE
    val isNeoForge get() = this == NEOFORGE
    val isForgeLike get() = this == FORGE || this == NEOFORGE
}
val loader = when (loaderName) {
    "common" -> Loader.COMMON
    "fabric" -> Loader.FABRIC
    "forge" -> Loader.FORGE
    "neoforge" -> Loader.NEOFORGE
    else -> throw AssertionError("invalid loader '$loaderName'")
}

fun Boolean.toInt() = if (this) 1 else 0

val mcVersion: Int by project.extra

preprocess {
    vars.put("MC", mcVersion)
    vars.put("FABRIC", loader.isFabric.toInt())
    vars.put("FORGE", loader.isForge.toInt())
    vars.put("NEOFORGE", loader.isNeoForge.toInt())
    vars.put("FORGELIKE", loader.isForgeLike.toInt())
}

@Suppress("PropertyName")
class Props {
    // automatically convert to other types from the string properties
    private inner class Prop {
        @Suppress("UNCHECKED_CAST")
        operator fun <T> getValue(thisRef: Any?, property: KProperty<*>): T = when (property.returnType.jvmErasure) {
            Boolean::class -> project.extra[property.name].toString().toBoolean()
            Int::class -> project.extra[property.name].toString().toInt()
            List::class -> project.extra[property.name].toString().split(',')
            else -> project.extra[property.name]
        } as T
    }
    private val prop = Prop()

    //// Global Properties ////
    val fabric_loader_version: String by prop

    val mod_id: String by prop
    val mod_name: String by prop
    val mod_authors: List<String> by prop
    val mod_version: String by prop
    val mod_description: String by prop
    val maven_group: String by prop
    val archives_base_name: String by prop
    val license: String by prop
    val homepage_url: String by prop
    val sources_url: String by prop
    val issues_url: String by prop

    val fabric_kotlin_version: String by prop
    val forge_kotlin_version: String by prop
    val mixinextras_version: String by prop
    val conditional_mixin_version: String by prop

    val run_with_compat_mods: Boolean by prop

    //// Version Specific Properties ////
    val minecraft_version: String by prop
    val yarn_mappings: String by prop

    val minecraft_version_range_fabric: String by prop
    val minecraft_version_range_forge: String by prop
    val forge_version: String by prop
    val forge_version_range: String by prop
    val neoforge_version: String by prop
    val neoforge_version_range: String by prop

    val cloth_version: String by prop
    val clumps_version: String by prop

    val early_loading_screen_version: String by prop
    val fabric_api_version: String by prop
    val modmenu_version: String by prop
    val trinkets_version: String by prop
    val cca_version: String by prop

    val curios_version: String by prop
}
val props: Props = Props()

loom {
    runConfigs.all {
        // to make sure it generates all "Minecraft Client (:subproject_name)" applications
        isIdeConfigGenerated = !loader.isCommon
        runDir = "../../run-$loaderName"
        vmArg("-Dmixin.debug.export=true")
    }

    if (loader.isForge) {
        forge.mixinConfigs = listOf(
            "${props.mod_id}.mixins.json",
            "${props.mod_id}-forge.mixins.json",
        )
        // workaround for https://github.com/SpongePowered/Mixin/issues/560
        // TODO: remove this when Mixin 0.8.6 is out or you find another proper fix
        forge.useCustomMixin = false
        @Suppress("UnstableApiUsage")
        mixin.useLegacyMixinAp = false
    }

    rootDir.resolve("src/main/resources/${props.mod_id}.accesswidener").let {
        if (it.exists()) {
            accessWidenerPath = it
        }
    }
}

repositories {
    when (loader) {
        Loader.COMMON -> {}
        Loader.FABRIC -> {
            // Mod Menu and Trinkets
            maven("https://maven.terraformersmc.com/releases/")
            // Cardinal Components (for Trinkets)
            maven("https://maven.ladysnake.org/releases")
        }
        Loader.NEOFORGE -> {
            // NeoForge
            maven("https://maven.neoforged.net/releases")
        }
        Loader.FORGE -> {
            // MixinExtras
            mavenCentral()
        }
    }
    if (!loader.isFabric) {
        // Kotlin for Forge
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        // Curios API
        maven("https://maven.theillusivec4.top/")
    }

    // Cloth Config
    maven("https://maven.shedaniel.me/")
    // Conditional Mixin
    maven("https://jitpack.io")
    maven("https://maven.fallenbreath.me/releases")
    // Other mods from Modrinth
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:${props.minecraft_version}")
    mappings("net.fabricmc:yarn:${props.yarn_mappings}:v2")

    // outside the fabric specific projects this should only be used for the @Environment annotation
    modImplementation("net.fabricmc:fabric-loader:${props.fabric_loader_version}")

    fun modCompat(dependencyNotation: String, dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}) =
        if (props.run_with_compat_mods) {
            modImplementation(dependencyNotation, dependencyConfiguration)
        } else {
            modCompileOnly(dependencyNotation, dependencyConfiguration)
        }

    when (loader) {
        Loader.COMMON -> {
            modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${props.cloth_version}") {
                exclude(group = "net.fabricmc.fabric-api")
            }
            modCompileOnly("me.fallenbreath:conditional-mixin-common:${props.conditional_mixin_version}")
        }
        Loader.FABRIC -> {
            modLocalRuntime("maven.modrinth:early-loading-screen:${props.early_loading_screen_version}")

            modImplementation("net.fabricmc.fabric-api:fabric-api:${props.fabric_api_version}")

            include(modImplementation("me.fallenbreath:conditional-mixin-fabric:${props.conditional_mixin_version}")!!)

            modImplementation("net.fabricmc:fabric-language-kotlin:${props.fabric_kotlin_version}")
            modImplementation("com.terraformersmc:modmenu:${props.modmenu_version}")
            modImplementation("me.shedaniel.cloth:cloth-config-fabric:${props.cloth_version}") {
                exclude(group = "net.fabricmc.fabric-api")
            }

            // other mods we do integration with
            // - Trinkets
            modCompat("dev.emi:trinkets:${props.trinkets_version}")
            // before 3.8.1 these weren't included as modApi but as modImplementation in Trinkets, so we must add them ourselves
            if (mcVersion < 12004) {
                modCompileOnly("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${props.cca_version}")
                modCompileOnly("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${props.cca_version}")
            }
        }
        Loader.FORGE -> {
            "forge"("net.minecraftforge:forge:${props.forge_version}")

            include(modImplementation("me.fallenbreath:conditional-mixin-forge:${props.conditional_mixin_version}")!!)

            implementation("thedarkcolour:kotlinforforge:${props.forge_kotlin_version}")
            modImplementation("me.shedaniel.cloth:cloth-config-forge:${props.cloth_version}")

            compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")!!)
            implementation(include("io.github.llamalad7:mixinextras-forge:0.3.5")!!)

            // other mods we do integration with
            // - Curios API
            modCompat("top.theillusivec4.curios:curios-forge:${props.curios_version}")
        }
        Loader.NEOFORGE -> {
            "neoForge"("net.neoforged:neoforge:${props.neoforge_version}")

            include(modImplementation("me.fallenbreath:conditional-mixin-neoforge:${props.conditional_mixin_version}")!!)

            implementation("thedarkcolour:kotlinforforge-neoforge:${props.forge_kotlin_version}")
            modImplementation("me.shedaniel.cloth:cloth-config-neoforge:${props.cloth_version}")

            // other mods we do integration with
            // - Curios API
            modCompat("top.theillusivec4.curios:curios-neoforge:${props.curios_version}")
        }
    }

    // other mods we do integration with
    modCompat("maven.modrinth:clumps:${props.clumps_version}")
}

var versionSuffix = ""
// detect github action environment variables
// https://docs.github.com/en/actions/learn-github-actions/environment-variables#default-environment-variables
if (System.getenv("BUILD_RELEASE") != "true") {
    val buildNumber = System.getenv("BUILD_ID")
    versionSuffix += buildNumber?.let { "+build.$it" } ?: "-SNAPSHOT"
}
val fullModVersion = props.mod_version + versionSuffix

tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val authors = props.mod_authors.joinToString(if (loader.isFabric) "\",\"" else ", ")

    val versionsMap = mapOf(
        11904 to 13,
        12001 to 15,
        12002 to 18,
        12004 to 22,
    )

    val replaceProperties = mapOf(
        "minecraft_version_range_fabric" to props.minecraft_version_range_fabric,
        "minecraft_version_range_forge" to props.minecraft_version_range_forge,
        "fabric_loader_version" to props.fabric_loader_version,
        "forge_version_range" to props.forge_version_range,
        "neoforge_version_range" to props.neoforge_version_range,
        "fabric_kotlin_version" to props.fabric_kotlin_version,
        "forge_kotlin_version" to props.forge_kotlin_version,
        "description" to props.mod_description,
        "homepage_url" to props.homepage_url,
        "sources_url" to props.sources_url,
        "issues_url" to props.issues_url,
        "mod_id" to props.mod_id,
        "mod_name" to props.mod_name,
        "version" to fullModVersion,
        "license" to props.license,
        "authors" to authors,
        "pack_format_number" to versionsMap[mcVersion],
    )
    inputs.properties(replaceProperties)

    filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }

    if (!loader.isFabric) {
        exclude {
            it.file.name == "fabric.mod.json"
        }
    }
}

// https://github.com/Fallen-Breath/yamlang
yamlang {
    targetSourceSets = listOf(sourceSets.main.get())
    inputDir = "assets/${props.mod_id}/lang"
}

base {
    archivesName = "${props.archives_base_name}-mc${props.minecraft_version}-$loaderName"
}

version = "v$fullModVersion"
group = props.maven_group

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
}

java {
    withSourcesJar()
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${props.archives_base_name}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    // select the repositories you want to publish to
    repositories {
        // uncomment to publish to the local maven
        // mavenLocal()
    }
}
