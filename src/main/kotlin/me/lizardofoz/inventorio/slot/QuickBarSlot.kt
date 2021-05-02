package me.lizardofoz.inventorio.slot

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.quickbar.QuickBarInventory
import me.lizardofoz.inventorio.util.QuickBarMode
import me.lizardofoz.inventorio.util.SlotRestrictionFilters
import me.lizardofoz.inventorio.util.isNotEmpty
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class QuickBarSlot(private val quickBarInventory: QuickBarInventory, private val shortCutIndex: Int, physicalInventory: Inventory, physicalIndex: Int, x: Int, y: Int) : Slot(physicalInventory, physicalIndex, x, y)
{
    var shortcutStack: ItemStack
        get() = quickBarInventory.getStack(shortCutIndex)
        set(value) = quickBarInventory.setStack(shortCutIndex, value)

    /**
     * Returns null if we want to proceed with vanilla slot behaviour.
     * Returns a value appropriate for vanilla [ScreenHandler.onSlotClick] otherwise
     */
    fun onSlotClick(clickData: Int, actionType: SlotActionType, playerAddon: PlayerAddon): ItemStack?
    {
        //If the Quick Bar is physical or there is a physical stack somehow, then let the Vanilla do its job
        //If the player is in Creative, they probably want slots to be treated as physical
        if (playerAddon.quickBarMode == QuickBarMode.PHYSICAL_SLOTS || getPhysicalStack().isNotEmpty || playerAddon.player.isCreative)
            return null

        val cursorStack = playerAddon.player.inventory.cursorStack
        var canPutNewStack = false

        //In this mode, anything goes onto the Quick Bar
        if (playerAddon.quickBarMode == QuickBarMode.NO_SPECIAL_CASES)
            canPutNewStack = true
        //In this mode, the Quick Bar allows some items to be stored physically.
        //If the cursor item is on that list, we let the Vanilla treat it as usual
        else if (playerAddon.quickBarMode == QuickBarMode.HANDLE_SPECIAL_CASES && SlotRestrictionFilters.physicalUtilityBar.invoke(cursorStack))
            return null
        //In the Default Quick Bar mode, if an item can go in Quick Bar as a shortcut, we put it there.
        else if (SlotRestrictionFilters.quickBar.invoke(cursorStack))
            canPutNewStack = true
        if (canPutNewStack)
            this.shortcutStack = cursorStack.copy()

        //Making a slot "do nothing" if we've overlooked something makes it less likely to cause a dupe.
        //Returning an item stack here cancels the vanilla behaviour.
        return cursorStack
    }

    override fun getStack(): ItemStack
    {
        val superStack = super.getStack()
        if (superStack.isNotEmpty)
            return superStack
        return shortcutStack
    }

    fun getPhysicalStack(): ItemStack
    {
        return super.getStack()
    }
}