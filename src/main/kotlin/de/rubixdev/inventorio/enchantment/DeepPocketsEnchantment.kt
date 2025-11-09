package de.rubixdev.inventorio.enchantment

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.util.DEEP_POCKETS_MAX_LEVEL
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.registry.tag.ItemTags

object DeepPocketsEnchantment : Enchantment(
    properties(
        ItemTags.LEG_ARMOR_ENCHANTABLE,
        5,
        DEEP_POCKETS_MAX_LEVEL,
        leveledCost(5, 8),
        leveledCost(55, 8),
        2,
        EquipmentSlot.LEGS,
    ),
) {
    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return GlobalSettings.deepPocketsInTrades.boolValue
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return GlobalSettings.deepPocketsInRandomSelection.boolValue
    }
}
