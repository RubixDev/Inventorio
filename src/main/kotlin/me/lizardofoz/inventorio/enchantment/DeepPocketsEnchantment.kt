package me.lizardofoz.inventorio.enchantment

import me.lizardofoz.inventorio.config.GlobalSettings
import me.lizardofoz.inventorio.util.DEEP_POCKETS_MAX_LEVEL
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot

object DeepPocketsEnchantment : Enchantment(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_LEGS, arrayOf(EquipmentSlot.LEGS)) {
    override fun getMinLevel(): Int {
        return 1
    }

    override fun getMaxLevel(): Int {
        return DEEP_POCKETS_MAX_LEVEL
    }

    override fun getMinPower(level: Int): Int {
        return 5 + (level - 1) * 8
    }

    override fun getMaxPower(level: Int): Int {
        return super.getMinPower(level) + 50
    }

    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return GlobalSettings.deepPocketsInTrades.boolValue
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return GlobalSettings.deepPocketsInRandomSelection.boolValue
    }
}
