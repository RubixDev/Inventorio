package de.rubixdev.inventorio.slot

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

open class BlockedSlot(inventory: Inventory, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y) {
    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        return false
    }

    override fun canInsert(stack: ItemStack): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return false
    }
}
