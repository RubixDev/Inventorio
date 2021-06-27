@file:JvmName("RandomStuff")

package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem

data class Point2I(@JvmField val x: Int, @JvmField val y: Int)
data class Point2F(@JvmField val x: Float, @JvmField val y: Float)
data class Rectangle(@JvmField val x: Int, @JvmField val y: Int, @JvmField val width: Int, @JvmField val height: Int)

enum class SegmentedHotbar
{
    OFF, ONLY_VISUAL, ON
}

enum class ScrollWheelUtilityBeltMode
{
    OFF, REGULAR, REVERSE
}

enum class DeepPocketsMode
{
    ENABLED, DISABLED_BUT_BIGGER_UTILITY, DISABLED;
}

interface ScreenHandlerDuck
{
    var screenHandlerAddon: PlayerScreenHandlerAddon?
}

interface InventoryDuck
{
    val inventorioAddon: PlayerInventoryAddon?
}

val ItemStack.isNotEmpty
    get() = !this.isEmpty

fun canRMBItem(itemStack: ItemStack): Boolean
{
    return PlayerSettings.canThrowUnloyalTrident.boolValue
            || itemStack.item !is TridentItem
            || EnchantmentHelper.getLoyalty(itemStack) > 0
            || EnchantmentHelper.getRiptide(itemStack) > 0
}