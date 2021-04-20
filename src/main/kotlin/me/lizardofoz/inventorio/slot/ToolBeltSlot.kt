package me.lizardofoz.inventorio.slot

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class ToolBeltSlot(inventory: Inventory, private val predicate: (ItemStack) -> Boolean, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y)
{
    override fun canInsert(stack: ItemStack): Boolean
    {
        return predicate.invoke(stack)
    }
}