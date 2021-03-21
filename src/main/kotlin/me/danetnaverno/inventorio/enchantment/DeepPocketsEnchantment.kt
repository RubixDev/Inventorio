package me.danetnaverno.inventorio.enchantment

import me.danetnaverno.inventorio.util.maxDeepPocketsLevel
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot

object DeepPocketsEnchantment : Enchantment(Rarity.RARE, EnchantmentTarget.ARMOR_LEGS, arrayOf(EquipmentSlot.LEGS))
{
    override fun getMinLevel(): Int
    {
        return 1
    }

    override fun getMaxLevel(): Int
    {
        return maxDeepPocketsLevel
    }

    override fun getMinPower(level: Int): Int
    {
        return level * 25
    }

    override fun getMaxPower(level: Int): Int
    {
        return getMinPower(level) + 50
    }

    override fun isTreasure(): Boolean
    {
        return true
    }
}