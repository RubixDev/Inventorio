package de.rubixdev.inventorio.client.ui

import de.rubixdev.inventorio.integration.trinkets.TrinketsInventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerInventory

open class InventorioScreen protected constructor(handler: InventorioScreenHandler, inventory: PlayerInventory) :
    AbstractInventorioScreen(handler, inventory) {
    companion object {
        @JvmStatic
        fun create(handler: InventorioScreenHandler, inventory: PlayerInventory) =
            when (FabricLoader.getInstance().isModLoaded("trinkets")) {
                true -> TrinketsInventorioScreen(handler, inventory)
                false -> InventorioScreen(handler, inventory)
            }
    }
}
