package de.rubixdev.inventorio.player

import net.minecraft.entity.player.PlayerInventory

/**
 * Small wrapper class which can be overwritten in submodules to allow for extra mod compat.
 */
open class InventorioScreenHandler protected constructor(syncId: Int, inventory: PlayerInventory) :
    AbstractInventorioScreenHandler(syncId, inventory) {
    companion object {
        @JvmStatic
        fun create(syncId: Int, inventory: PlayerInventory) = InventorioScreenHandler(syncId, inventory)
    }
}
