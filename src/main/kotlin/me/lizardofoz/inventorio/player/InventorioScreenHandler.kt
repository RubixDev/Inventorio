package me.lizardofoz.inventorio.player

import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.minecraft.entity.player.PlayerInventory

@Deprecated("Package has been moved", ReplaceWith("de.rubixdev.inventorio.player.InventorioScreenHandler"), DeprecationLevel.ERROR)
class InventorioScreenHandler(syncId: Int, inventory: PlayerInventory) : InventorioScreenHandler(syncId, inventory) {
    companion object {
        @JvmStatic
        @Suppress("DEPRECATION_ERROR")
        @Deprecated("For internal use only", level = DeprecationLevel.ERROR)
        fun of(delegate: InventorioScreenHandler?): me.lizardofoz.inventorio.player.InventorioScreenHandler? {
            return delegate?.let {
                me.lizardofoz.inventorio.player.InventorioScreenHandler(it.syncId, it.inventory).apply {
                    slots.indices.forEach { idx -> slots[idx] = it.slots[idx] }
                }
            }
        }
    }
}
