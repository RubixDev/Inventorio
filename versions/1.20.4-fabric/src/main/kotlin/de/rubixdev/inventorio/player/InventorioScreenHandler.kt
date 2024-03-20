package de.rubixdev.inventorio.player

import de.rubixdev.inventorio.integration.trinkets.TrinketsInventorioScreenHandler
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerInventory

open class InventorioScreenHandler protected constructor(syncId: Int, inventory: PlayerInventory) :
    AbstractInventorioScreenHandler(syncId, inventory) {
    companion object {
        @JvmStatic
        fun create(syncId: Int, inventory: PlayerInventory) =
            when (FabricLoader.getInstance().isModLoaded("trinkets")) {
                // TODO: is there a better solution than this that scales with multiple impls?
                true -> TrinketsInventorioScreenHandler(syncId, inventory)
                false -> InventorioScreenHandler(syncId, inventory)
            }
    }
}
