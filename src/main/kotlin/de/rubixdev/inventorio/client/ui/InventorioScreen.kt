package de.rubixdev.inventorio.client.ui

import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.minecraft.entity.player.PlayerInventory

/**
 * Small wrapper class which can be overwritten in submodules to allow for extra mod compat.
 */
open class InventorioScreen protected constructor(handler: InventorioScreenHandler, inventory: PlayerInventory) :
    AbstractInventorioScreen(handler, inventory) {
    companion object {
        @JvmStatic
        fun create(handler: InventorioScreenHandler, inventory: PlayerInventory) = InventorioScreen(handler, inventory)
    }
}
