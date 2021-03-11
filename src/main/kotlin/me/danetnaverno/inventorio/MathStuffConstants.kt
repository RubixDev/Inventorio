package me.danetnaverno.inventorio

import me.danetnaverno.inventorio.enchantment.DeepPocketsEnchantment
import me.danetnaverno.inventorio.quickbar.QuickBarInventory
import me.danetnaverno.inventorio.util.SlotRestrictionFilters
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

const val maxDeepPocketsLevel = 3
const val vanillaRowLength = 9
const val inventorioRowLength = 12
const val mainInventorySize = inventorioRowLength * 3
const val maxExtensionSlots = maxDeepPocketsLevel * inventorioRowLength

const val armorLength = 4
val toolbeltLength = SlotRestrictionFilters.toolBelt.size
const val utilityBarLength = 8

const val gui_canvas_physical_quickbarW = 216
const val gui_canvas_physical_quickbarH = 18
const val gui_canvas_inventorySlotSize = 18
const val gui_canvas_container_gap_height = 17

const val gui_generalInventoryWidth = 230
const val gui_inventoryBottomPartStart = 83
const val gui_toolBeltStartX = 134
const val gui_toolBeltStartY = 62
const val gui_utilityBeltStartX = 76
const val gui_utilityBeltStartY = 7
const val gui_physical_quickbarX = 7
const val gui_physical_quickbarY = 141

const val gui_container_slotStartX = 36

const val canvas_containerTopPartHeight = 139
const val canvas_containerBottomPartStart = canvas_containerTopPartHeight

const val canvas_inventoryGap = 4
const val canvas_containerGap = 24
const val canvas_inventoryTopPartHeight = 83
const val canvas_inventoryBottomPartStart = canvas_inventoryTopPartHeight
const val canvas_utilityBeltStartX = 76
const val canvas_utilityBeltStartY = 7
const val canvas_inventoryExtensionStart = 198
const val canvas_toolBeltExtStartX = 238
const val canvas_toolBeltExtStartY = 184
const val canvas_utility_frameX = 234
const val canvas_utility_frameY = 162
const val canvas_utility_frameW = 22
const val canvas_utility_frameH = 22

const val canvas_toolbeltSlotX = 240
const val canvas_toolbeltSlotY = 82

const val canvas_physicalQuickBarX = 7
const val canvas_physicalQuickBarY = 198

const val canvas_quickBarStartX = 0
const val canvas_quickBarStartY = 0
const val canvas_quickBarWidth = 242
const val canvas_splitQuickBarStartX = 0
const val canvas_splitQuickBarStartY = 46
const val canvas_splitQuickBarWidth = 254
const val canvas_qbSectionSelectionX = 152
const val canvas_qbSectionSelectionY = 22
const val canvas_qbSectionSelectionW = 84
const val canvas_qbSectionSelectionH = 24
const val canvas_single_slotX = 189
const val canvas_single_slotY = 166
const val canvas_physical_quickbarX = 7
const val canvas_physical_quickbarY = 198

val mainSlotsRange = 0 until mainInventorySize
val armorSlotsRange = mainSlotsRange.last + 1 until (mainSlotsRange.last + 1) + armorLength
val dudOffhandRange = armorSlotsRange.last + 1 .. armorSlotsRange.last + 1
val extensionSlotsRange = dudOffhandRange.last + 1 until (dudOffhandRange.last + 1) + maxExtensionSlots
val toolBeltSlotsRange = extensionSlotsRange.last + 1 until (extensionSlotsRange.last + 1) + toolbeltLength
val utilityBarSlotsRange = toolBeltSlotsRange.last + 1 until (toolBeltSlotsRange.last + 1) + utilityBarLength
val quickBarPhysicalSlotsRange = utilityBarSlotsRange.last + 1 until (utilityBarSlotsRange.last + 1) + inventorioRowLength
val quickBarShortcutSlotsRange = quickBarPhysicalSlotsRange.last + 1 until (quickBarPhysicalSlotsRange.last + 1) + inventorioRowLength
val craftGridSlotsRange = quickBarShortcutSlotsRange.last + 1 until (quickBarShortcutSlotsRange.last + 1) + 5

val quickBarContainerPhysicalSlotsRange = quickBarPhysicalSlotsRange.first + 100 until quickBarPhysicalSlotsRange.last + 100
val quickBarContainerShortcutSlotsRange = quickBarContainerPhysicalSlotsRange.first + quickBarContainerPhysicalSlotsRange.count() until quickBarContainerPhysicalSlotsRange.last + quickBarContainerPhysicalSlotsRange.count()

val ItemStack.isNotEmpty: Boolean
    get() = !this.isEmpty

val Slot.isPlayerSlot: Boolean
    get() = this.inventory is PlayerInventory || this.inventory is QuickBarInventory

/**
 * The reason why we have three objects here is because slot indexes are DIFFERENT for
 */
object MathStuffConstants
{
    fun getAvailableExtensionSlotsRange(player: PlayerEntity): IntRange
    {
        return extensionSlotsRange.first until extensionSlotsRange.first + getExtraSlots(player)
    }

    fun getUnavailableExtensionSlotsRange(player: PlayerEntity): IntRange
    {
        return getAvailableExtensionSlotsRange(player).last + 1 .. extensionSlotsRange.last
    }

    fun getExtraSlots(player: PlayerEntity): Int
    {
        return getExtraRows(player) * inventorioRowLength
    }

    fun getExtraRows(player: PlayerEntity): Int
    {
        return EnchantmentHelper.getEquipmentLevel(DeepPocketsEnchantment, player)
    }

    fun getExtraPixelHeight(player: PlayerEntity): Int
    {
        return getExtraRows(player) * gui_canvas_inventorySlotSize
    }

    fun getMainInventoryStartHeight(player: PlayerEntity): Int
    {
        val height = getExtraPixelHeight(player)
        return 1 + canvas_inventoryTopPartHeight + if (height == 0) 0 else height + canvas_inventoryGap
    }

    fun getExpandedInventoryStartHeight(player: PlayerEntity): Int
    {
        return 1 + canvas_inventoryTopPartHeight
    }
}

enum class QuickBarMode
{
    DEFAULT, NO_SPECIAL_CASES, HANDLE_SPECIAL_CASES, PHYSICAL_SLOTS, NOT_SELECTED
}

enum class UtilityBeltMode
{
    FILTERED, UNFILTERED
}

enum class QuickBarSimplified
{
    OFF, ONLY_VISUAL, ON
}