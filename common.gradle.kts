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

val modBrand = if (project.name.endsWith("-common")) "common" else loom.platform.get().name.lowercase()
assert(modBrand in listOf("common", "fabric", "forge", "neoforge"))
assert(project.name.endsWith("-$modBrand"))

val mcVersion: Int by project.extra

preprocess {
    vars.put("MC", mcVersion)
    vars.put("FABRIC", if (modBrand == "fabric") 1 else 0)
    vars.put("FORGE", if (modBrand == "forge") 1 else 0)
    vars.put("NEOFORGE", if (modBrand == "neoforge") 1 else 0)
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

    val fabric_api_version: String by prop
    val modmenu_version: String by prop
}
val props: Props = Props()

loom {
    runConfigs.all {
        // to make sure it generates all "Minecraft Client (:subproject_name)" applications
        isIdeConfigGenerated = modBrand != "common"
        runDir = "../../run-$modBrand"
        vmArg("-Dmixin.debug.export=true")
    }

    if (modBrand == "forge") {
        forge.mixinConfigs = listOf("${props.mod_id}.mixins.json")
    }

    rootDir.resolve("src/main/resources/${props.mod_id}.accesswidener").let {
        if (it.exists()) {
            accessWidenerPath = it
        }
    }
}

repositories {
    when (modBrand) {
        "fabric" -> {
            // Mod Menu
            maven("https://maven.terraformersmc.com/releases/")
        }
        "neoforge" -> {
            // NeoForge
            maven("https://maven.neoforged.net/releases")
        }
        "forge" -> {
            // MixinExtras
            mavenCentral()
        }
    }
    if (modBrand != "fabric") {
        // Kotlin for Forge
        maven("https://thedarkcolour.github.io/KotlinForForge/")
    }

    // Cloth Config
    maven("https://maven.shedaniel.me/")
    // Other mods from Modrinth
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:${props.minecraft_version}")
    mappings("net.fabricmc:yarn:${props.yarn_mappings}:v2")

    // outside the fabric specific projects this should only be used for the @Environment annotation
    modImplementation("net.fabricmc:fabric-loader:${props.fabric_loader_version}")

    when (modBrand) {
        "common" -> {
            modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${props.cloth_version}") {
                exclude(group = "net.fabricmc.fabric-api")
            }
        }
        "fabric" -> {
            modImplementation("net.fabricmc.fabric-api:fabric-api:${props.fabric_api_version}")

            modImplementation("net.fabricmc:fabric-language-kotlin:${props.fabric_kotlin_version}")
            modImplementation("com.terraformersmc:modmenu:${props.modmenu_version}")
            modImplementation("me.shedaniel.cloth:cloth-config-fabric:${props.cloth_version}") {
                exclude(group = "net.fabricmc.fabric-api")
            }
        }
        "forge" -> {
            "forge"("net.minecraftforge:forge:${props.forge_version}")

            implementation("thedarkcolour:kotlinforforge:${props.forge_kotlin_version}")
            modImplementation("me.shedaniel.cloth:cloth-config-forge:${props.cloth_version}")

            compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")!!)
            implementation(include("io.github.llamalad7:mixinextras-forge:0.3.5")!!)
        }
        "neoforge" -> {
            "neoForge"("net.neoforged:neoforge:${props.neoforge_version}")

            implementation("thedarkcolour:kotlinforforge-neoforge:${props.forge_kotlin_version}")
            modImplementation("me.shedaniel.cloth:cloth-config-neoforge:${props.cloth_version}")
        }
    }

    // other mods we do integration with
    modCompileOnly("maven.modrinth:clumps:${props.clumps_version}")
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

    val authors = props.mod_authors.joinToString(if (modBrand == "fabric") "\",\"" else ", ")

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

    if (modBrand != "fabric") {
        exclude {
            it.file.name == "fabric.mod.json"
        }
    }
}

base {
    archivesName = "${props.archives_base_name}-mc${props.minecraft_version}-$modBrand"
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
