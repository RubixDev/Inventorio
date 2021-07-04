package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.player.InventorioScreenHandler
import me.lizardofoz.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Consumer

object MixinHelpers
{
    @JvmStatic
    @JvmOverloads
    fun withInventoryAddon(playerEntity: PlayerEntity?, consumer: Consumer<PlayerInventoryAddon>, ifNotPresent: Consumer<PlayerEntity?> = Consumer { })
    {
        val addon = playerEntity?.inventoryAddon
        if (addon != null)
            consumer.accept(addon)
        else
            ifNotPresent.accept(playerEntity)
    }

    @JvmStatic
    fun withScreenHandler(playerEntity: PlayerEntity?, consumer: Consumer<InventorioScreenHandler>)
    {
        val addon = playerEntity?.inventorioScreenHandler
        if (addon != null)
            consumer.accept(addon)
    }
}