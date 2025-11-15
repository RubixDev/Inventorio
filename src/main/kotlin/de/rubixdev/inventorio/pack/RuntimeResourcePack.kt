package de.rubixdev.inventorio.pack

import com.google.gson.Gson
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import de.rubixdev.inventorio.util.logger
import java.io.InputStream
import java.util.Optional
import java.util.stream.Stream
import net.minecraft.SharedConstants
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder
import net.minecraft.data.server.recipe.SmithingTrimRecipeJsonBuilder
import net.minecraft.recipe.Recipe
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.VersionedIdentifier
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.entry.RegistryEntryOwner
import net.minecraft.registry.tag.TagEntry
import net.minecraft.registry.tag.TagFile
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.InputSupplier
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackInfo
import net.minecraft.resource.ResourcePackSource
import net.minecraft.resource.ResourceType
import net.minecraft.resource.metadata.PackFeatureSetMetadata
import net.minecraft.resource.metadata.PackOverlaysMetadata
import net.minecraft.resource.metadata.PackResourceMetadata
import net.minecraft.resource.metadata.ResourceFilter
import net.minecraft.resource.metadata.ResourceMetadataReader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

//#if NEOFORGE
//$$ import net.neoforged.neoforge.common.conditions.ICondition
//#endif

//#if MC >= 12101
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.RegistryKeys
//#else
//$$ import net.minecraft.registry.tag.TagManagerLoader
//#endif

private typealias Resource = InputSupplier<InputStream>
private typealias DirectoryMap = MutableMap<String, PackEntry>
private data class ResourceEntry(val resource: Resource) : PackEntry()
private data class DirectoryEntry(val nested: DirectoryMap = mutableMapOf()) : PackEntry()

private sealed class PackEntry {
    fun asResource(): Resource? = (this as? ResourceEntry)?.resource
    fun asDirectory(): DirectoryMap? = (this as? DirectoryEntry)?.nested

    fun find(path: List<String>): PackEntry? =
        path.fold(this as PackEntry?) { entry, segment -> entry?.asDirectory()?.get(segment) }

    fun findAllResources(path: List<String> = listOf(), consumer: (path: List<String>, resource: Resource) -> Unit): Unit =
        asDirectory()?.entries?.forEach { (name, entry) ->
            when (entry) {
                is ResourceEntry -> consumer(path + name, entry.resource)
                is DirectoryEntry -> entry.findAllResources(path + name, consumer)
            }
        } ?: Unit
}

class RuntimeResourcePack(
    private val info: ResourcePackInfo,
    val metadata: PackResourceMetadata,
    val features: PackFeatureSetMetadata? = null,
    val filter: ResourceFilter? = null,
    val overlays: PackOverlaysMetadata? = null,
    // TODO: client-only metadata (LanguageResourceMetadata, GuiResourceMetadata, VillagerResourceMetadata, AnimationResourceMetadata, TextureResourceMetadata)
    private val extraFiles: Map<List<String>, InputSupplier<InputStream>> = mapOf(),
) : ResourcePack {
    companion object {
        private val GSON = Gson()

        //#if MC >= 12101
        private val RegistryKey<out Registry<*>>.path get() = RegistryKeys.getPath(this).split("/")
        private val RegistryKey<out Registry<*>>.tagPath get() = RegistryKeys.getTagPath(this).split("/")
        //#else
        //$$ private val RegistryKey<out Registry<*>>.path get() = value.splitPath
        //$$ private val RegistryKey<out Registry<*>>.tagPath get() = TagManagerLoader.getPath(this).split("/")
        //#endif
        private val Identifier.splitPath get() = path.split("/")
        private val List<String>.withJsonExt get() = dropLast(1) + "${last()}.json"

        val SOURCE = object : ResourcePackSource {
            override fun decorate(packDisplayName: Text): Text =
                Text.translatable("pack.nameAndSource", packDisplayName, Text.of("Runtime generated"))
            override fun canBeEnabledLater(): Boolean = true
        }

        fun createInfo(id: Identifier, title: Text, version: String) = ResourcePackInfo(
            id.path,
            title,
            SOURCE,
            Optional.of(VersionedIdentifier(id.namespace, id.path, version)),
        )

        fun createMetadata(description: Text, type: ResourceType = ResourceType.SERVER_DATA) = PackResourceMetadata(
            description,
            SharedConstants.getGameVersion().getResourceVersion(type),
            Optional.empty(),
        )
    }

    private val root = DirectoryEntry()

    // TODO: is this needed for anything but a pack.png
    override fun openRoot(vararg segments: String): InputSupplier<InputStream>? =
        extraFiles[segments.toList()]

    override fun open(
        type: ResourceType,
        id: Identifier,
    ): InputSupplier<InputStream>? {
        val path = listOf(type.directory, id.namespace) + id.path.split("/")
        return root.find(path)?.asResource()
    }

    override fun findResources(
        type: ResourceType,
        namespace: String,
        prefix: String,
        consumer: ResourcePack.ResultConsumer,
    ) {
        val basePath = listOf(type.directory, namespace) + prefix.split("/")
        root.find(basePath)?.findAllResources { path, resource ->
            val id = Identifier.tryParse(namespace, (basePath.drop(2) + path).joinToString("/")) ?: run {
                logger.error("Invalid path in pack: {}:{}, ignoring", namespace, path.joinToString("/"))
                return@findAllResources
            }
            consumer.accept(id, resource)
        }
    }

    override fun getNamespaces(type: ResourceType): Set<String> =
        root.nested[type.directory]?.asDirectory()?.keys ?: setOf()

    override fun <T : Any> parseMetadata(metaReader: ResourceMetadataReader<T>): T? =
        @Suppress("UNCHECKED_CAST")
        when (metaReader.key) {
            PackResourceMetadata.SERIALIZER.key -> metadata as T
            PackFeatureSetMetadata.SERIALIZER.key -> features as T?
            ResourceFilter.SERIALIZER.key -> filter as T?
            PackOverlaysMetadata.SERIALIZER.key -> overlays as T?
            else -> null
        }

    override fun getInfo(): ResourcePackInfo = info

    override fun close() {}

    fun <T> addResource(
        type: ResourceType,
        registry: RegistryKey<out Registry<T>>,
        codec: Codec<T>,
        id: Identifier,
        value: T,
    ): RegistryKey<T> = RegistryKey.of(registry, id).also {
        addResource(type, registry.path, codec, id, value)
    }

    fun <T> addResource(
        type: ResourceType,
        path: List<String>,
        codec: Codec<T>,
        id: Identifier,
        value: T,
    ) {
        val path = listOf(id.namespace) + path + id.splitPath.withJsonExt
        val json = GSON.toJson(codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow())
        logger.debug("adding resource:\n{}\n{}", path.joinToString("/"), json)
        addResource(type, path, json)
    }

    fun addResource(type: ResourceType, path: List<String>, resource: String) =
        addResource(type, path) { resource.byteInputStream() }

    fun addResource(type: ResourceType, path: List<String>, resource: Resource) {
        val fullPath = listOf(type.directory) + path

        // ensure directory exists
        val dirPath = fullPath.dropLast(1)
        val dir = dirPath.fold(root as PackEntry) { entry, segment ->
            when (entry) {
                is ResourceEntry -> throw IllegalArgumentException("Part of resource path is already stored as a resource")
                is DirectoryEntry -> entry.nested.getOrPut(segment, ::DirectoryEntry)
            }
        }.asDirectory() ?: throw IllegalArgumentException("Part of resource path is already stored as a resource")

        // insert (or overwrite) resource entry
        dir[path.last()] = ResourceEntry(resource)
    }

    ////// Tags //////

    inline fun <T> addItemsToTag(tagKey: TagKey<T>, tagBuilder: TagBuilder<T>.() -> Unit) =
        addItemsToTag(tagKey, TagBuilder<T>().apply(tagBuilder))

    fun <T> addItemsToTag(tagKey: TagKey<T>, tagBuilder: TagBuilder<T>) =
        addItemsToTag(tagKey, tagBuilder.build())

    fun <T> addItemsToTag(tagKey: TagKey<T>, tagEntries: List<TagEntry>, replace: Boolean = false) =
        addItemsToTag(tagKey, TagFile(tagEntries, replace))

    fun <T> addItemsToTag(tagKey: TagKey<T>, tagFile: TagFile): TagKey<T> {
        val path = mutableListOf<String>().apply {
            add(tagKey.id.namespace)
            addAll(tagKey.registry.tagPath)
            addAll(tagKey.id.splitPath.withJsonExt)
        }
        val json = GSON.toJson(TagFile.CODEC.encodeStart(JsonOps.INSTANCE, tagFile).getOrThrow())
        logger.debug("adding tag:\n{}\n{}", path.joinToString("/"), json)
        addResource(ResourceType.SERVER_DATA, path, json)
        return tagKey
    }

    ////// Enchantments //////

    //#if MC >= 12101
    fun addEnchantment(id: Identifier, enchantment: Enchantment.Definition) =
        addEnchantment(id, Enchantment.builder(enchantment))

    fun addEnchantment(id: Identifier, enchantment: Enchantment.Builder) =
        addEnchantment(id, enchantment.build(id))

    fun addEnchantment(id: Identifier, enchantment: Enchantment) =
        addResource(ResourceType.SERVER_DATA, RegistryKeys.ENCHANTMENT, Enchantment.CODEC, id, enchantment)
    //#endif

    ////// Recipes and Advancements //////

    fun addRecipe(id: Identifier, recipe: Recipe<*>) =
        //#if MC >= 12101
        addResource(ResourceType.SERVER_DATA, RegistryKeys.RECIPE, Recipe.CODEC, id, recipe)
        //#else
        //$$ addResource(ResourceType.SERVER_DATA, listOf("recipes"), Recipe.CODEC, id, recipe)
        //#endif

    fun addAdvancement(id: Identifier, advancementBuilder: Advancement.Builder) =
        addAdvancement(advancementBuilder.build(id))

    fun addAdvancement(entry: AdvancementEntry) =
        addAdvancement(entry.id, entry.value)

    fun addAdvancement(id: Identifier, advancement: Advancement) =
        //#if MC >= 12101
        addResource(ResourceType.SERVER_DATA, RegistryKeys.ADVANCEMENT, Advancement.CODEC, id, advancement)
        //#else
        //$$ addResource(ResourceType.SERVER_DATA, listOf("advancements"), Advancement.CODEC, id, advancement)
        //#endif

    val recipeExporter by lazy {
        object : RecipeExporter {
            override fun accept(recipeId: Identifier, recipe: Recipe<*>, advancement: AdvancementEntry?) {
                addRecipe(recipeId, recipe)
                if (advancement != null) addAdvancement(advancement)
            }

            override fun getAdvancementBuilder(): Advancement.Builder =
                Advancement.Builder.createUntelemetered().parent(AdvancementEntry(CraftingRecipeJsonBuilder.ROOT, null))

            //#if NEOFORGE
            //$$ override fun accept(recipeId: Identifier, recipe: Recipe<*>, advancement: AdvancementEntry?, vararg conditions: ICondition) =
            //$$     accept(recipeId, recipe, advancement)
            //#endif
        }
    }

    val recipeExporterOnlyRecipe by lazy {
        object : RecipeExporter {
            override fun accept(recipeId: Identifier, recipe: Recipe<*>, advancement: AdvancementEntry?) {
                addRecipe(recipeId, recipe)
            }

            override fun getAdvancementBuilder(): Advancement.Builder = Advancement.Builder.createUntelemetered()

            //#if NEOFORGE
            //$$ override fun accept(recipeId: Identifier, recipe: Recipe<*>, advancement: AdvancementEntry?, vararg conditions: ICondition) =
            //$$     accept(recipeId, recipe, advancement)
            //#endif
        }
    }

    val recipeExporterOnlyAdvancement by lazy {
        object : RecipeExporter {
            override fun accept(recipeId: Identifier, recipe: Recipe<*>, advancement: AdvancementEntry?) {
                if (advancement != null) addAdvancement(advancement)
            }

            override fun getAdvancementBuilder(): Advancement.Builder =
                Advancement.Builder.createUntelemetered().parent(AdvancementEntry(CraftingRecipeJsonBuilder.ROOT, null))

            //#if NEOFORGE
            //$$ override fun accept(recipeId: Identifier, recipe: Recipe<*>, advancement: AdvancementEntry?, vararg conditions: ICondition) =
            //$$     accept(recipeId, recipe, advancement)
            //#endif
        }
    }

    fun addRecipeAndAdvancement(recipeId: Identifier, builder: CraftingRecipeJsonBuilder) =
        builder.offerTo(recipeExporter, recipeId)

    fun addRecipeAndAdvancement(recipeId: Identifier, builder: SmithingTransformRecipeJsonBuilder) =
        builder.offerTo(recipeExporter, recipeId)

    fun addRecipeAndAdvancement(recipeId: Identifier, builder: SmithingTrimRecipeJsonBuilder) =
        builder.offerTo(recipeExporter, recipeId)

    fun addRecipeAndAdvancement(recipeId: Identifier, builder: ComplexRecipeJsonBuilder) =
        builder.offerTo(recipeExporter, recipeId)

    //#if MC >= 12101
    fun addRecipe(recipeId: Identifier, builder: CraftingRecipeJsonBuilder): RegistryKey<Recipe<*>> =
        builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { RegistryKey.of(RegistryKeys.RECIPE, recipeId) }

    fun addRecipe(recipeId: Identifier, builder: SmithingTransformRecipeJsonBuilder): RegistryKey<Recipe<*>> =
        builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { RegistryKey.of(RegistryKeys.RECIPE, recipeId) }

    fun addRecipe(recipeId: Identifier, builder: SmithingTrimRecipeJsonBuilder): RegistryKey<Recipe<*>> =
        builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { RegistryKey.of(RegistryKeys.RECIPE, recipeId) }

    fun addRecipe(recipeId: Identifier, builder: ComplexRecipeJsonBuilder): RegistryKey<Recipe<*>> =
        builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { RegistryKey.of(RegistryKeys.RECIPE, recipeId) }
    //#else
    //$$ fun addRecipe(recipeId: Identifier, builder: CraftingRecipeJsonBuilder) =
    //$$     builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { recipeId }
    //$$
    //$$ fun addRecipe(recipeId: Identifier, builder: SmithingTransformRecipeJsonBuilder) =
    //$$     builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { recipeId }
    //$$
    //$$ fun addRecipe(recipeId: Identifier, builder: SmithingTrimRecipeJsonBuilder) =
    //$$     builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { recipeId }
    //$$
    //$$ fun addRecipe(recipeId: Identifier, builder: ComplexRecipeJsonBuilder) =
    //$$     builder.offerTo(recipeExporterOnlyRecipe, recipeId).let { recipeId }
    //#endif

    fun addAdvancement(recipeId: Identifier, builder: CraftingRecipeJsonBuilder) =
        builder.offerTo(recipeExporterOnlyAdvancement, recipeId)

    fun addAdvancement(recipeId: Identifier, builder: SmithingTransformRecipeJsonBuilder) =
        builder.offerTo(recipeExporterOnlyAdvancement, recipeId)

    fun addAdvancement(recipeId: Identifier, builder: SmithingTrimRecipeJsonBuilder) =
        builder.offerTo(recipeExporterOnlyAdvancement, recipeId)

    fun addAdvancement(recipeId: Identifier, builder: ComplexRecipeJsonBuilder) =
        builder.offerTo(recipeExporterOnlyAdvancement, recipeId)

    fun advancementBuilderForRecipe(recipe: RegistryKey<out Recipe<*>>): Advancement.Builder =
        advancementBuilderForRecipe(recipe.value)

    fun advancementBuilderForRecipe(recipeId: Identifier): Advancement.Builder =
        recipeExporterOnlyAdvancement.advancementBuilder
            .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId))
            .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
}

class TagBuilder<T> {
    private val builder = net.minecraft.registry.tag.TagBuilder()
    private var replace = false

    fun setReplace(replace: Boolean) = also { this.replace = replace }

    fun add(entry: TagEntry) = also { builder.add(entry) }
    fun add(id: Identifier) = also { builder.add(id) }
    fun add(key: RegistryKey<T>) = also { builder.add(key.value) }

    fun addOptional(id: Identifier) = also { builder.addOptional(id) }
    fun addOptional(key: RegistryKey<T>) = also { builder.addOptional(key.value) }

    fun addTag(id: Identifier) = also { builder.addTag(id) }
    fun addTag(key: TagKey<T>) = also { builder.addTag(key.id) }

    fun addOptionalTag(id: Identifier) = also { builder.addOptionalTag(id) }
    fun addOptionalTag(key: TagKey<T>) = also { builder.addOptionalTag(key.id) }

    fun build() = TagFile(builder.build(), replace)
}

class DummyRegistryEntryList<T>(val tag: TagKey<T>) : RegistryEntryList<T> {
    override fun stream(): Stream<RegistryEntry<T>> = Stream.empty()
    override fun size(): Int = 0
    override fun getStorage(): Either<TagKey<T>, List<RegistryEntry<T>>> = Either.left(tag)
    override fun getRandom(random: Random): Optional<RegistryEntry<T>> = Optional.empty()
    override fun get(index: Int): RegistryEntry<T>? = null
    override fun contains(entry: RegistryEntry<T>): Boolean = false
    override fun ownerEquals(owner: RegistryEntryOwner<T>): Boolean = true
    override fun getTagKey(): Optional<TagKey<T>> = Optional.of(tag)
    override fun iterator(): MutableIterator<RegistryEntry<T>> = mutableListOf<RegistryEntry<T>>().iterator()
}
