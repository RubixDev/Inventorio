package me.lizardofoz.inventorio.integration

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.guavy.gravestones.api.GravestonesApi
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ItemScatterer

class GravestonesIntegration : GravestonesApi
{
    override fun getInventory(entity: PlayerEntity): List<ItemStack>
    {
        return entity.inventoryAddon?.stacks ?: emptyList()
    }

    override fun setInventory(inventory: MutableList<ItemStack>, entity: PlayerEntity)
    {
        val x = entity.pos.x
        val y = entity.pos.y
        val z = entity.pos.z
        for (itemStack in inventory)
            ItemScatterer.spawn(entity.world, x, y, z, itemStack)
    }

    override fun getInventorySize(entity: PlayerEntity): Int
    {
        return entity.inventoryAddon?.stacks?.size ?: 0
    }
}