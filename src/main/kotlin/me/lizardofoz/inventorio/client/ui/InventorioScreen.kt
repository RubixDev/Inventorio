package me.lizardofoz.inventorio.client.ui

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.mixin.client.accessor.HandledScreenAccessor
import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.minecraft.entity.player.PlayerInventory

@Deprecated("Package has been moved", ReplaceWith("de.rubixdev.inventorio.client.ui.InventorioScreen"), DeprecationLevel.ERROR)
class InventorioScreen(handler: InventorioScreenHandler, inventory: PlayerInventory) :
    InventorioScreen(handler, inventory) {
    companion object {
        @JvmStatic
        @Suppress("DEPRECATION_ERROR", "DeprecatedCallableAddReplaceWith")
        @Deprecated("For internal use only", level = DeprecationLevel.ERROR)
        fun of(delegate: InventorioScreen?): me.lizardofoz.inventorio.client.ui.InventorioScreen? {
            return delegate?.let {
                @Suppress("UNCHECKED_CAST")
                me.lizardofoz.inventorio.client.ui.InventorioScreen(
                    (it as HandledScreenAccessor<InventorioScreenHandler>).handler,
                    it.inventory,
                )
            }
        }
    }
}
