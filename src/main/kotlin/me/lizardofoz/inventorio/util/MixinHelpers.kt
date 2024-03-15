package me.lizardofoz.inventorio.util

import java.util.function.Consumer
import me.lizardofoz.inventorio.player.InventorioScreenHandler
import me.lizardofoz.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.entity.player.PlayerEntity

object MixinHelpers {
    @JvmStatic
    @JvmOverloads
    fun <T> withInventoryAddonReturning(
        playerEntity: PlayerEntity?,
        consumer: (PlayerInventoryAddon) -> T?,
        ifNotPresent: (PlayerEntity?) -> T? = { null },
    ): T? {
        val addon = playerEntity?.inventoryAddon
        return if (addon != null) {
            consumer(addon)
        } else {
            ifNotPresent(playerEntity)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun withInventoryAddon(
        playerEntity: PlayerEntity?,
        consumer: Consumer<PlayerInventoryAddon>,
        ifNotPresent: Consumer<PlayerEntity?> = Consumer {},
    ) {
        withInventoryAddonReturning(playerEntity, { addon -> consumer.accept(addon) }, { player -> ifNotPresent.accept(player) })
    }

    @JvmStatic
    @JvmOverloads
    fun withScreenHandler(
        playerEntity: PlayerEntity?,
        consumer: Consumer<InventorioScreenHandler>,
        ifNotPresent: Consumer<PlayerEntity?> = Consumer {},
    ) {
        val addon = playerEntity?.inventorioScreenHandler
        if (addon != null) {
            consumer.accept(addon)
        } else {
            ifNotPresent.accept(playerEntity)
        }
    }
}
