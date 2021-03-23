package me.danetnaverno.inventorio.util

import me.danetnaverno.inventorio.enchantment.DeepPocketsEnchantment
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.quickbar.QuickBarInventory
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

const val DEEP_POCKETS_MAX_LEVEL = 3
const val VANILLA_ROW_LENGTH = 9
const val INVENTORIO_ROW_LENGTH = 12

const val INVENTORY_SLOT_SIZE = 18

const val MAIN_INVENTORY_SIZE = INVENTORIO_ROW_LENGTH * 3
const val EXTENSION_SIZE = DEEP_POCKETS_MAX_LEVEL * INVENTORIO_ROW_LENGTH
const val ARMOR_SIZE = 4
const val TOOL_BELT_SIZE = 5
const val UTILITY_BELT_SIZE = 8


val MAIN_INVENTORY_RANGE = 0 until MAIN_INVENTORY_SIZE
val ARMOR_RANGE = MAIN_INVENTORY_RANGE.last + 1 until (MAIN_INVENTORY_RANGE.last + 1) + ARMOR_SIZE
val DUD_OFFHAND_RANGE = ARMOR_RANGE.last + 1 .. ARMOR_RANGE.last + 1
val EXTENSION_RANGE = DUD_OFFHAND_RANGE.last + 1 until (DUD_OFFHAND_RANGE.last + 1) + EXTENSION_SIZE
val TOOL_BELT_RANGE = EXTENSION_RANGE.last + 1 until (EXTENSION_RANGE.last + 1) + TOOL_BELT_SIZE
val UTILITY_BELT_RANGE = TOOL_BELT_RANGE.last + 1 until (TOOL_BELT_RANGE.last + 1) + UTILITY_BELT_SIZE
val QUICK_BAR_PHYS_RANGE = UTILITY_BELT_RANGE.last + 1 until (UTILITY_BELT_RANGE.last + 1) + INVENTORIO_ROW_LENGTH
val QUICK_BAR_SHORTCUTS_RANGE = QUICK_BAR_PHYS_RANGE.last + 1 until (QUICK_BAR_PHYS_RANGE.last + 1) + INVENTORIO_ROW_LENGTH
val CRAFTING_GRID_RANGE = QUICK_BAR_SHORTCUTS_RANGE.last + 1 until (QUICK_BAR_SHORTCUTS_RANGE.last + 1) + 5
val UTILITY_BELT_EXTENSION_RANGE = UTILITY_BELT_RANGE.first + 4 .. UTILITY_BELT_RANGE.last

val ItemStack.isNotEmpty: Boolean
    get() = !this.isEmpty

val Slot.isPlayerSlot: Boolean
    get() = this.inventory is PlayerInventory || this.inventory is QuickBarInventory


/**
 * The reason why we have three objects here is because slot indexes are DIFFERENT for
 */
object GeneralConstants
{
    fun getAvailableExtensionSlotsRange(player: PlayerEntity): IntRange
    {
        return EXTENSION_RANGE.first until EXTENSION_RANGE.first + getExtraSlots(player)
    }

    fun getUnavailableExtensionSlotsRange(player: PlayerEntity): IntRange
    {
        return getAvailableExtensionSlotsRange(player).last + 1 .. EXTENSION_RANGE.last
    }

    fun getExtraSlots(player: PlayerEntity): Int
    {
        return getExtraRows(player) * INVENTORIO_ROW_LENGTH
    }

    fun getExtraRows(player: PlayerEntity): Int
    {
        return EnchantmentHelper.getEquipmentLevel(DeepPocketsEnchantment, player)
    }

    fun getExtraPixelHeight(player: PlayerEntity): Int
    {
        return getExtraRows(player) * INVENTORY_SLOT_SIZE
    }

    fun canPlayerStoreItemStackPhysicallyInQuickBar(player: PlayerEntity, itemStack: ItemStack): Boolean
    {
        val resMode = PlayerAddon[player].quickBarMode
        return resMode == QuickBarMode.PHYSICAL_SLOTS ||
                (resMode == QuickBarMode.HANDLE_SPECIAL_CASES && SlotRestrictionFilters.physicalUtilityBar.invoke(itemStack))
    }
}