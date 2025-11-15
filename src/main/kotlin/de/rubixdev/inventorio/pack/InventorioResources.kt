package de.rubixdev.inventorio.pack

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.enchantment.DeepPocketsBookRecipe
import de.rubixdev.inventorio.util.MOD_ID
import de.rubixdev.inventorio.util.PlatformApi
import de.rubixdev.inventorio.util.id
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.text.Text

//#if MC >= 12101
import de.rubixdev.inventorio.util.DEEP_POCKETS_MAX_LEVEL
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.EnchantmentTags
import net.minecraft.registry.tag.ItemTags
//#endif

object InventorioResources {
    @JvmField
    val PACK = RuntimeResourcePack(
        RuntimeResourcePack.createInfo(
            "inventorio_runtime".id,
            Text.of("Inventorio Runtime Data"),
            PlatformApi.modVersion(MOD_ID)!!,
        ),
        RuntimeResourcePack.createMetadata(Text.of("Runtime generated data used by Inventorio")),
    )

    //#if MC >= 12101
    val DEEP_POCKETS: RegistryKey<Enchantment> = PACK.addEnchantment(
        "deep_pockets".id,
        Enchantment.definition(
            DummyRegistryEntryList(ItemTags.LEG_ARMOR_ENCHANTABLE),
            5,
            DEEP_POCKETS_MAX_LEVEL,
            Enchantment.leveledCost(5, 8),
            Enchantment.leveledCost(55, 8),
            2,
            AttributeModifierSlot.LEGS,
        ),
    )
    //#endif

    init {
        //#if MC >= 12101
        if (GlobalSettings.deepPocketsInTrades.boolValue
            && GlobalSettings.deepPocketsInRandomSelection.boolValue
            && GlobalSettings.deepPocketsInEnchantingTable.boolValue
        ) {
            PACK.addItemsToTag(EnchantmentTags.NON_TREASURE) { add(DEEP_POCKETS) }
        } else {
            if (GlobalSettings.deepPocketsInTrades.boolValue) {
                PACK.addItemsToTag(EnchantmentTags.TRADEABLE) { add(DEEP_POCKETS) }
                PACK.addItemsToTag(EnchantmentTags.ON_TRADED_EQUIPMENT) { add(DEEP_POCKETS) }
            }
            if (GlobalSettings.deepPocketsInRandomSelection.boolValue) {
                PACK.addItemsToTag(EnchantmentTags.ON_RANDOM_LOOT) { add(DEEP_POCKETS) }
            }
            if (GlobalSettings.deepPocketsInEnchantingTable.boolValue) {
                PACK.addItemsToTag(EnchantmentTags.IN_ENCHANTING_TABLE) { add(DEEP_POCKETS) }
            }
        }
        //#endif

        if (GlobalSettings.deepPocketsBookCraft.boolValue) {
            val deepPocketsRecipe = PACK.addRecipe(
                "deep_pockets_book".id,
                ComplexRecipeJsonBuilder.create(::DeepPocketsBookRecipe),
            )
            PACK.addAdvancement(
                "unlock_deep_pockets_book".id(),
                PACK.advancementBuilderForRecipe(deepPocketsRecipe)
                    .criterion("tick", TickCriterion.Conditions.createTick()),
            )
        }
    }
}
