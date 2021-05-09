package me.lizardofoz.inventorio.slot

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

/**
 * Minecraft has hardcoded slot indexes (40 for inventory and 45 for screen handler) for the offhand.
 * This is a hack to counter-act any potential unaccounted Mojang hardcoding related to the offhand
 */
class DudOffhandSlot(inventory: Inventory, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y)
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
    }
}
   