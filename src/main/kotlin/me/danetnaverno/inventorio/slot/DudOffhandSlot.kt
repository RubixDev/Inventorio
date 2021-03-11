package me.danetnaverno.inventorio.slot

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class DudOffhandSlot(inventory: Inventory, index: Int, x: Int, y: Int): Slot(inventory, index, x, y)
{
    override fun canTakeItems(playerEntity: PlayerEntity): Boolean
    {
        return false
    }

    override fun canInsert(stack: ItemStack): Boolean
    {
        return false
    }

    @Environment(EnvType.CLIENT)
    override fun doDrawHoveringEffect(): Boolean
    {
        return false
    }

    override fun getMaxItemCount(): Int
    {
        return 0
    }

    override fun getMaxItemCount(stack: ItemStack?): Int
    {
        return 0
    }

    override fun setStack(stack: ItemStack)
    {
        //throw IllegalAccessException("Setting a stack of a dud offhand isn't allowed")
    }
}
   