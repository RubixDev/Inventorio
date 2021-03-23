package me.danetnaverno.inventorio.quickbar

import me.danetnaverno.inventorio.mixin.ScreenHandlerAccessor
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.player.PlayerInventoryAddon
import me.danetnaverno.inventorio.slot.QuickBarPhysicalSlot
import me.danetnaverno.inventorio.slot.QuickBarShortcutSlot
import me.danetnaverno.inventorio.util.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

class QuickBarHandlerWidget(val inventoryAddon: PlayerInventoryAddon)
{
    fun createQuickBarSlots(handler: ScreenHandler, startX: Int, startY: Int, slotIndicesRange: IntRange)
    {
        val accessor = handler as ScreenHandlerAccessor
        val shortCutSlots = mutableListOf<QuickBarShortcutSlot>()
        //Physical QuickBar
        for ((absolute, relative) in slotIndicesRange.withRelativeIndex())
        {
            val shortCutSlot = QuickBarShortcutSlot(inventoryAddon.shortcutQuickBar, relative,
                    startX + relative * INVENTORY_SLOT_SIZE,
                    startY)
            shortCutSlots.add(shortCutSlot)
            accessor.addASlot(QuickBarPhysicalSlot(shortCutSlot, inventoryAddon.inventory, absolute,
                    startX + relative * INVENTORY_SLOT_SIZE,
                    startY))
        }

        //Shortcut QuickBar
        for (shortCutSlot in shortCutSlots)
            accessor.addASlot(shortCutSlot)
    }

    fun onSlotClick(handler: ScreenHandler, slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
    {
        if (slotIndex !in 0 until handler.slots.size)
            return null
        val slot = handler.getSlot(slotIndex)
        if (slot is QuickBarPhysicalSlot)
        {
            val currentStack = slot.stack
            val cursor = player.inventory.cursorStack
            val shortCutSlot = slot.shortcutSlot

            if (cursor.isEmpty)
            {
                if (!GeneralConstants.canPlayerStoreItemStackPhysicallyInQuickBar(player, currentStack))
                    shortCutSlot.stack = ItemStack.EMPTY
                return null
            }
            else if (GeneralConstants.canPlayerStoreItemStackPhysicallyInQuickBar(player, cursor))
            {
                shortCutSlot.stack = ItemStack.EMPTY
                return null
            }
            else
            {
                if (PlayerAddon[player].quickBarMode != QuickBarMode.DEFAULT
                        || !SlotRestrictionFilters.physicalUtilityBar.invoke(cursor))
                    shortCutSlot.stack = cursor.copy()
                return cursor
            }
        }
        return null
    }
}