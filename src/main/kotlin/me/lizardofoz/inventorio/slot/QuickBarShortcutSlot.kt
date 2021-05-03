package me.lizardofoz.inventorio.slot

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.QuickBarMode
import me.lizardofoz.inventorio.util.SlotRestrictionFilters
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class QuickBarShortcutSlot(inventory: Inventory, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y)
{
    companion object
    {
        /**
         * Returns null if we want to proceed with vanilla slot behaviour.
         * Returns a value appropriate for vanilla [ScreenHandler.onSlotClick] otherwise
         */
        fun clickShortCutSlot(handler: ScreenHandler, slotIndex: Int, clickData: Int, actionType: SlotActionType, playerAddon: PlayerAddon): ItemStack?
        {
            if (playerAddon.quickBarMode == QuickBarMode.PHYSICAL_SLOTS)
                return null

            val sourceSlot = if (slotIndex < 0 || slotIndex>=handler.slots.size) null else handler.getSlot(slotIndex)
            val targetSlot = if (clickData < 0 || clickData>=handler.slots.size) null else handler.getSlot(clickData)

            if (actionType == SlotActionType.THROW && sourceSlot is QuickBarShortcutSlot)
                return ItemStack.EMPTY
            if (actionType == SlotActionType.SWAP && targetSlot is QuickBarShortcutSlot)
            {
                val sourceStack = sourceSlot?.stack ?: ItemStack.EMPTY
                if (playerAddon.quickBarMode == QuickBarMode.UNFILTERED || SlotRestrictionFilters.quickBar.invoke(sourceStack))
                {
                    if (sourceSlot is QuickBarShortcutSlot)
                        sourceSlot.stack = targetSlot.stack.copy()
                    targetSlot.stack = sourceStack.copy()
                    return ItemStack.EMPTY
                }
            }
            if (sourceSlot is QuickBarShortcutSlot)
            {
                val cursorStack = playerAddon.player.inventory.cursorStack
                if (playerAddon.quickBarMode == QuickBarMode.UNFILTERED || SlotRestrictionFilters.quickBar.invoke(cursorStack))
                {
                    sourceSlot.stack = cursorStack.copy()
                    return cursorStack
                }
            }
            return null
        }
    }
}