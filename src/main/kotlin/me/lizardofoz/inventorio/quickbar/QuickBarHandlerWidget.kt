package me.lizardofoz.inventorio.quickbar

import me.lizardofoz.inventorio.mixin.accessor.ScreenHandlerAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.slot.QuickBarPhysicalSlot
import me.lizardofoz.inventorio.slot.QuickBarShortcutSlot
import me.lizardofoz.inventorio.util.INVENTORY_SLOT_SIZE
import me.lizardofoz.inventorio.util.QuickBarMode
import me.lizardofoz.inventorio.util.SlotRestrictionFilters
import me.lizardofoz.inventorio.util.withRelativeIndex
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

@Deprecated("Delete Me")
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

            if (player.isCreative)
            {
                if (cursor.isEmpty)
                {
                    player.inventory.cursorStack = shortCutSlot.stack.copy()
                    shortCutSlot.stack = ItemStack.EMPTY
                }
                else
                {
                    shortCutSlot.stack = cursor.copy()
                    player.inventory.cursorStack = ItemStack.EMPTY
                }
                return cursor
            }

            if (cursor.isEmpty)
            {
                if (!SlotRestrictionFilters.canPlayerStoreItemStackPhysicallyInQuickBar(player, currentStack))
                    shortCutSlot.stack = ItemStack.EMPTY
                return null
            }
            else if (SlotRestrictionFilters.canPlayerStoreItemStackPhysicallyInQuickBar(player, cursor))
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