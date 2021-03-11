package me.danetnaverno.inventorio.util

import me.danetnaverno.inventorio.QuickBarMode
import me.danetnaverno.inventorio.player.PlayerAddon
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object PhysicalQuickBarLogic
{
    fun canPlayerStoreItemStackPhysicallyInQuickBar(player: PlayerEntity, itemStack: ItemStack): Boolean
    {
        val resMode = PlayerAddon[player].quickBarMode
        return resMode == QuickBarMode.PHYSICAL_SLOTS ||
                (resMode == QuickBarMode.HANDLE_SPECIAL_CASES && SlotRestrictionFilters.physicalUtilityBar.invoke(itemStack))
    }
}