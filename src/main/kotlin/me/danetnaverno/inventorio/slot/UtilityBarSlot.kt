package me.danetnaverno.inventorio.slot

import me.danetnaverno.inventorio.UtilityBeltMode
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.SlotRestrictionFilters
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

class UtilityBarSlot(inventory: PlayerInventory, index: Int, x: Int, y: Int) : ExtensionSlot(inventory, index, x, y)
{
    override fun canInsert(stack: ItemStack): Boolean
    {
        if (!canTakeItems)
            return false
        if (SlotRestrictionFilters.utilityBelt.invoke(stack))
            return true
        val playerAddon = PlayerAddon[(inventory as PlayerInventory).player]
        return playerAddon.utilityBeltMode == UtilityBeltMode.UNFILTERED
    }
}